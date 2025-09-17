package com.example.book.service;

import com.example.book.dto.BookingChangeRequestDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Service
public class BookingChangeRequestService {
    private final BookingChangeRequestRepository bcrr;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public BookingChangeRequestService(BookingChangeRequestRepository bcrr, BookingRepository bookingRepository, BookingService bookingService, ServiceRepository serviceRepository, UserRepository userRepository) {
        this.bcrr = bcrr;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingChangeRequest proposeChangeAsService(Long id, BookingChangeRequestDto input, String email){
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(()-> new RuntimeException("Booking Not Found"));
        com.example.book.model.Service service = serviceRepository.findById(booking.getService().getId()).orElseThrow(()-> new RuntimeException("Service Not Found"));

        if(!Objects.equals(owner.getId(), service.getUser().getId())){
            throw new RuntimeException("Service Owner By User");
        }
        try {
            bookingService.bookingChecks(booking, owner, service);
        }catch (Exception e){
            throw new RuntimeException("Error With Booking");
        }

        BookingChangeRequest bcr = new BookingChangeRequest();
        bcr.setBooking(booking);
        bcr.setUser(booking.getUser());
        bcr.setCurrent_date(booking.getDate());
        bcr.setCurrent_start(booking.getStart());
        bcr.setCurrent_end(booking.getEnd());
        bcr.setProposed_date(LocalDate.parse(input.getProposed_date()));
        bcr.setProposed_start(LocalTime.parse(input.getProposed_start()));
        bcr.setProposed_end(LocalTime.parse(input.getProposed_end()));
        bcr.setStatus(BookingStatus.PENDING);
        bcr.setReason(input.getReason());
        bcr.setExpires_at(LocalDateTime.now().plusDays(1));

        bcrr.save(bcr);

        return bcr;
    }

    @Transactional
    public Booking acceptProposal(Long id, String email){
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(()-> new RuntimeException("Booking Not Found"));
        com.example.book.model.Service service = serviceRepository.findById(booking.getService().getId()).orElseThrow(()-> new RuntimeException("Service Not Found"));

        if(!Objects.equals(owner.getId(), service.getUser().getId())){
            throw new RuntimeException("Service Owner By User");
        }
        try {
            bookingService.bookingChecks(booking, owner, service);
        }catch (Exception e){
            throw new RuntimeException("Error With Booking");
        }

        BookingChangeRequest bcr = new BookingChangeRequest();
        bcr.setBooking(booking);
        bcr.setUser(booking.getUser());
        bcr.setCurrent_date(booking.getDate());
        bcr.setCurrent_start(booking.getStart());
        bcr.setCurrent_end(booking.getEnd());
        bcr.setProposed_date(LocalDate.parse(input.getProposed_date()));
        bcr.setProposed_start(LocalTime.parse(input.getProposed_start()));
        bcr.setProposed_end(LocalTime.parse(input.getProposed_end()));
        bcr.setStatus(BookingStatus.PENDING);
        bcr.setReason(input.getReason());
        bcr.setExpires_at(LocalDateTime.now().plusDays(1));

        bcrr.save(bcr);

        return bcr;
    }


}
