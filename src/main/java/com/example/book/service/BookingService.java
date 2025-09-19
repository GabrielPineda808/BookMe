package com.example.book.service;

import com.example.book.dto.BookingDto;
import com.example.book.exception.*;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, ServiceRepository serviceRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Booking createBooking(BookingDto input, String email){

        if (!input.getEnd().isAfter(input.getStart())) {
            throw new BookingTimeValidationException("End time must be after start time.");
        }


        //Booking is within service hours

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        System.out.println("User found");

        com.example.book.model.Service service = serviceRepository.findById(input.getServiceId())
                .orElseThrow(()-> new ServiceNotFoundException("Service Not Found"));

        long minutesDifference = Math.abs(ChronoUnit.MINUTES.between(input.getStart(), input.getEnd()));
        if(minutesDifference != service.getInterval()){
            throw new BookingDurationException("Booking duration does not match Service interval");
        }



        boolean overlaps = bookingRepository.existsOverlapping(
                service.getId(),input.getDate(), input.getStart(), input.getEnd());

        if (overlaps) {
            throw new BookingOverlapException("Time slot overlaps an existing booking.");
        }

        Booking booking = new Booking();
        booking.setService(service);
        booking.setDate(input.getDate());
        booking.setStart(input.getStart());
        booking.setEnd(input.getEnd());
        booking.setUser(owner);
        booking.setNotes(input.getNotes());
        booking.setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);
        System.out.println("saving booking");
        return booking;
    }

    @Transactional
    public Booking manageBooking(Long id, String email, String action){
        Booking booking = bookingRepository.findById(id).orElseThrow(()->new BookingNotFoundException("Booking Not Found"));
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User Not Found"));
        com.example.book.model.Service service = booking.getService();

        if(bookingChecks(booking,owner,service)) {
            //checking wether service booked is owned by user
            boolean serviceOwner = booking.getService().getUser().getId().equals(owner.getId());
            if(!serviceOwner){
                if(action.equals("cancel")){
                    booking.setStatus(BookingStatus.CANCELLED);
                    return bookingRepository.save(booking);
                }
                throw new BookingOwnershipException("Booking does not belong to service owner.");
            }else {
                if (action.equals("accept")) {
                    booking.setStatus(BookingStatus.CONFIRMED);
                } else if (action.equals("decline")) {
                    booking.setStatus(BookingStatus.DECLINED);
                } else if (action.equals("cancel")) {
                    booking.setStatus(BookingStatus.CANCELLED);
                }
                return bookingRepository.save(booking);
            }
        }else {
            throw new BookingManagementException("Booking Management Not Available");
        }
    }

    public Boolean bookingChecks(Booking booking, User owner, com.example.book.model.Service service){
        //Booking still pending check
        if(!booking.getStatus().equals(BookingStatus.PENDING) && !booking.getStatus().equals(BookingStatus.CONFIRMED)){
            throw new BookingStatusException("Booking has either been DECLINED or CANCELLED");
        }

        // checking that booking has not passed booking date requested & the time is an hour befoer booking minimum
        //Advance acceptance buffer
        //
        //I already enforce a minimum 1-hour buffer. In the future Some services require more (e.g. 24 hours for catering).
        //When i create services i may need to add what their minimum booking buffer is so that we dont display available booking times to the customer
        //that do not align with its minimum prep time.
        // allow accepting only if it's at least 60 min before start
        // Lead-time / already-ended check

        LocalDateTime now   = LocalDateTime.now(); // use ZonedDateTime if TZ matters
        LocalDateTime start = LocalDateTime.of(booking.getDate(), booking.getStart());
        LocalDateTime end   = LocalDateTime.of(booking.getDate(), booking.getEnd());

        if (now.isAfter(end) || !now.isBefore(start.minusHours(1))) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            throw new BookingExpiredException("Booking has expired or is too close to start time");
        }

        // Service constraints
        LocalTime open  = service.getOpen();
        LocalTime close = service.getClose();
        int intervalMin = service.getInterval();
        if (intervalMin <= 0) {
            throw new ServiceIntervalException("Service interval must be > 0");
        }

        // Within hours (inclusive)
        LocalTime bStart = booking.getStart();
        LocalTime bEnd   = booking.getEnd();
        if (bStart.isBefore(open) || bEnd.isAfter(close)) {
            throw new ServiceHoursException("Booking time is outside service hours");
        }

        // Duration must be positive
        long durationMin = Duration.between(bStart, bEnd).toMinutes();
        if (durationMin <= 0) {
            throw new BookingDurationException("End must be after start");
        }

        // Start must align to interval grid from 'open'
        long minutesFromOpen = Duration.between(open, bStart).toMinutes();
        if (Math.floorMod(minutesFromOpen, intervalMin) != 0) {
            throw new BookingTimeValidationException("Start time not aligned to interval");
        }

        // Duration must align to interval (allow multi-slot)
        if (durationMin % intervalMin != 0) {
            throw new BookingTimeValidationException("Duration must be a multiple of the interval");
        }

        //No overlapping accepted booking for the same service
        boolean overlaps = bookingRepository.existsOverlapping(
                service.getId(),booking.getDate(), booking.getStart(), booking.getEnd());

        //checking if bookings overlap
        if (overlaps) {
            throw new BookingOverlapException("Time slot overlaps an existing booking.");
        }

        //User account is active
        if(!owner.isEnabled()){
            throw new UserNotActiveException("User Not Active");
        }

        // Capacity / Resource Validation
        //
        //Resource availability
        //
        //If services can be tied to a physical resource (room, field, chair, equipment), ensure that resource isn’t already in use for the requested time.
        //
        //Max concurrent bookings per owner/service
        //
        //If owners can accept multiple bookings (e.g., multiple staff under one account), check that the service isn’t over capacity.
        //
        //Extra Safety & Logging
        //
        //Prevent race conditions
        //
        //If two owners/admins act on the same booking at the same time, ensure only one action succeeds.
        //
        //Do this with an @Transactional service layer method and check booking status right before updating.
        //
        //Audit/log acceptance
        //
        //Log who accepted the booking and when, so disputes can be resolved.
        return true;
    }

    public List<Booking> getBookingsByOwner(String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return bookingRepository.findByUser(owner);
    }

    public List<Booking> getBookingsByService(Long id, String email) {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return serviceRepository.findBookingsByService(owner, id);
    }
}
