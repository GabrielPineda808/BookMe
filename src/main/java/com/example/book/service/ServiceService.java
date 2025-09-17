package com.example.book.service;

import com.example.book.dto.LocationDto;
import com.example.book.dto.ServiceDto;
import com.example.book.exception.*;
import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;

import com.example.book.model.User;
import com.example.book.repository.BookingChangeRequestRepository;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingChangeRequestRepository bookingChangeRequestRepository;

    public ServiceService(ServiceRepository serviceRepository, UserRepository userRepository, 
                         BookingRepository bookingRepository, BookingChangeRequestRepository bookingChangeRequestRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingChangeRequestRepository = bookingChangeRequestRepository;
    }

    @Transactional
    public com.example.book.model.Service createService(ServiceDto input, String email){
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate handle 
        if (serviceRepository.existsByHandle(input.getHandle())) {
            throw new ServiceHandleExistsException("Service handle already exists");
        }

        com.example.book.model.Service service = new com.example.book.model.Service();
        service.setUser(owner);
        service.setService_name(input.getService_name());
        service.setHandle(input.getHandle());
        service.setDesc(input.getDesc());
        service.setOpen(input.getOpen());
        service.setClose(input.getClose());
        service.setInterval(input.getInterval());
        
        if (input.getLocation() != null) {
            LocationDto loc = new LocationDto();
            loc.setAddress(input.getLocation().getAddress());
            loc.setCity(input.getLocation().getCity());
            loc.setState(input.getLocation().getState());
            loc.setArea_code(input.getLocation().getArea_code());
            loc.setCountry(input.getLocation().getCountry());
            service.setLocation(loc);
        }
        
        return serviceRepository.save(service);
    }

    @Transactional
    public com.example.book.model.Service updateService(Long serviceId, ServiceDto input, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        com.example.book.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        // Verify ownership
        if (!Objects.equals(service.getUser().getId(), owner.getId())) {
            throw new ServiceNotOwnedByUserException("Only the service owner can update the service");
        }

        // Check if handle is being changed and validate uniqueness
        if (!service.getHandle().equals(input.getHandle()) && serviceRepository.existsByHandle(input.getHandle())) {
            throw new ServiceHandleExistsException("Service handle already exists");
        }

        // Store old values for old booking checks
        LocalTime oldOpen = service.getOpen();
        LocalTime oldClose = service.getClose();
        int oldInterval = service.getInterval();

        // Update service 
        service.setService_name(input.getService_name());
        service.setHandle(input.getHandle());
        service.setDesc(input.getDesc());
        service.setOpen(input.getOpen());
        service.setClose(input.getClose());
        service.setInterval(input.getInterval());

        if (input.getLocation() != null) {
            LocationDto loc = new LocationDto();
            loc.setAddress(input.getLocation().getAddress());
            loc.setCity(input.getLocation().getCity());
            loc.setState(input.getLocation().getState());
            loc.setArea_code(input.getLocation().getArea_code());
            loc.setCountry(input.getLocation().getCountry());
            service.setLocation(loc);
        }

        // Check for impact on existing bookings
        List<Booking> affectedBookings = checkBookingImpact(service, oldOpen, oldClose, oldInterval);
        
        if (!affectedBookings.isEmpty()) {
            // Handle affected bookings - you might want to notify users or create change requests
            handleAffectedBookings(service, affectedBookings);
        }

        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long serviceId, String email, boolean forceDelete) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        com.example.book.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        // Verify ownership
        if (!Objects.equals(service.getUser().getId(), owner.getId())) {
            throw new ServiceNotOwnedByUserException("Only the service owner can delete the service");
        }

        // Check for active bookings
        long activeBookingsCount = serviceRepository.countActiveBookings(serviceId);
        
        if (activeBookingsCount > 0 && !forceDelete) {
            throw new ActiveBookingsException("Cannot delete service with active bookings. Use force delete to proceed, but this will cancel all bookings.");
        }

        if (activeBookingsCount > 0 && forceDelete) {
            // Cancel all active bookings
            List<Booking> activeBookings = bookingRepository.findByServiceIdAndStatusIn(serviceId, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));
            
            for (Booking booking : activeBookings) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }

            // Cancel all pending change requests
            List<BookingChangeRequest> pendingChangeRequests = bookingChangeRequestRepository.findAll().stream()
                .filter(bcr -> bcr.getBooking().getService().getId().equals(serviceId))
                .filter(bcr -> bcr.getStatus() == BookingStatus.PENDING)
                .toList();

            for (BookingChangeRequest bcr : pendingChangeRequests) {
                bcr.setStatus(BookingStatus.CANCELLED);
                bcr.setResponse_reason("Service deleted");
                bookingChangeRequestRepository.save(bcr);
            }
        }

        // Delete the service
        serviceRepository.delete(service);
    }

    public com.example.book.model.Service getServiceById(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));
    }

    public List<com.example.book.model.Service> getServicesByOwner(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return serviceRepository.findByUser(owner);
    }

    public List<com.example.book.model.Service> getServicesByCity(String city) {
        return serviceRepository.findByCity(city);
    }

    public List<com.example.book.model.Service> getServicesByState(String state) {
        return serviceRepository.findByState(state);
    }

    public com.example.book.model.Service getServiceByHandle(String handle) {
        return serviceRepository.findByHandle(handle)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));
    }

    public ServiceImpactAnalysis analyzeServiceUpdateImpact(Long serviceId, ServiceDto proposedChanges) {
        com.example.book.model.Service currentService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        List<Booking> affectedBookings = checkBookingImpact(currentService, 
            proposedChanges.getOpen(), proposedChanges.getClose(), proposedChanges.getInterval());

        return new ServiceImpactAnalysis(affectedBookings.size(), affectedBookings);
    }

    private List<Booking> checkBookingImpact(com.example.book.model.Service service, LocalTime newOpen, LocalTime newClose, int newInterval) {
        // Get all confirmed and pending bookings for this service
        List<Booking> bookings = bookingRepository.findByServiceIdAndStatusIn(
            service.getId(), List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));

        return bookings.stream()
            .filter(booking -> {
                // Check if booking times are outside new service hours
                LocalTime bookingStart = booking.getStart();
                LocalTime bookingEnd = booking.getEnd();
                
                return bookingStart.isBefore(newOpen) || 
                       bookingEnd.isAfter(newClose) ||
                       !isValidInterval(bookingStart, bookingEnd, newOpen, newInterval);
            })
            .toList();
    }

    private boolean isValidInterval(LocalTime start, LocalTime end, LocalTime serviceOpen, int interval) {
        // Check if booking duration is a multiple of the new interval
        long durationMinutes = java.time.Duration.between(start, end).toMinutes();
        return durationMinutes % interval == 0;
    }

    private void handleAffectedBookings(com.example.book.model.Service service, List<Booking> affectedBookings) {
        // Send notifications to customers
        // Create automatic change requests
        // Log the changes for audit purposes
        
        //i need to decide whether i want to keep old booking times or not and not let them change as they were made previously 
        //either automatic chagne request or if it does not affect next or previous booking change it to the new service times
        //for now i will just log the affected bookings
        for (Booking booking : affectedBookings) {
            System.out.println("Booking " + booking.getId() + " may be affected by service changes");
        }
    }

    // Inner class for analysis
    public static class ServiceImpactAnalysis {
        private final int affectedBookingsCount;
        private final List<Booking> affectedBookings;

        public ServiceImpactAnalysis(int affectedBookingsCount, List<Booking> affectedBookings) {
            this.affectedBookingsCount = affectedBookingsCount;
            this.affectedBookings = affectedBookings;
        }

        public int getAffectedBookingsCount() {
            return affectedBookingsCount;
        }

        public List<Booking> getAffectedBookings() {
            return affectedBookings;
        }
    }
}
