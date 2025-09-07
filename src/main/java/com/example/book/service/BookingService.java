package com.example.book.service;

import com.example.book.dto.BookingDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        System.out.println("booking service");
        if (!input.getEnd().isAfter(input.getStart())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        com.example.book.model.Service service = serviceRepository.findById(input.getServiceId())
                .orElseThrow(()-> new RuntimeException("Service Not Found"));

        boolean overlaps = bookingRepository.existsOverlapping(
                service.getId(),input.getDate(), input.getStart(), input.getEnd());

        if (overlaps) {
            throw new IllegalStateException("Time slot overlaps an existing booking.");
        }

        Booking booking = new Booking();
        booking.setService(service);
        booking.setDate(input.getDate());
        booking.setStart(input.getStart());
        booking.setEnd(input.getEnd());
        booking.setUser(owner);
        booking.setNotes(input.getNotes());
        booking.setBookingStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);
        System.out.println("saving booking");
        return booking;
    }
}
