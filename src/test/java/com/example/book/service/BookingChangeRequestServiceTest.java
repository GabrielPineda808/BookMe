package com.example.book.service;

import com.example.book.dto.BookingChangeRequestDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.repository.BookingChangeRequestRepository;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingChangeRequestServiceTest {

    @Mock
    BookingChangeRequestRepository bcrRepo;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingService bookingService;
    @Mock
    ServiceRepository serviceRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingChangeRequestService bcrService;

    private User owner;
    private User customer;
    private Service service;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setRole(Role.USER);
        owner.setEnabled(true);

        customer = new User();
        customer.setId(2L);
        customer.setEmail("customer@example.com");
        customer.setRole(Role.USER);
        customer.setEnabled(true);

        service = new Service();
        service.setId(10L);
        service.setUser(owner);
        service.setInterval(60);
        service.setOpen(LocalTime.of(9, 0));
        service.setClose(LocalTime.of(17, 0));
        service.setEnabled(true);

        booking = new Booking();
        booking.setId(5L);
        booking.setUser(customer);
        booking.setService(service);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));
    }

    @Test
    void proposeChangeAsService_createsPendingRequest() {
        BookingChangeRequestDto input = new BookingChangeRequestDto();
        input.setBooking_id(booking.getId());
        input.setProposed_date(LocalDate.now().plusDays(2).toString());
        input.setProposed_start("12:00");
        input.setProposed_end("13:00");
        input.setReason("Need to shift schedule");

        when(userRepository.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
        when(bookingService.bookingChecks(any(), any(), any())).thenReturn(true);
        when(bcrRepo.save(any(BookingChangeRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingChangeRequest request = bcrService.proposeChangeAsService(booking.getId(), input, owner.getEmail());

        assertEquals(BookingStatus.PENDING, request.getStatus());
        assertEquals(booking.getDate(), request.getCurrent_date());
        assertEquals(LocalDate.parse(input.getProposed_date()), request.getProposed_date());
        verify(bcrRepo).save(any(BookingChangeRequest.class));
    }

    @Test
    void acceptProposal_updatesBookingAndRequest() {
        BookingChangeRequest changeRequest = new BookingChangeRequest();
        changeRequest.setId(9L);
        changeRequest.setBooking(booking);
        changeRequest.setUser(customer);
        changeRequest.setStatus(BookingStatus.PENDING);
        changeRequest.setProposed_date(LocalDate.now().plusDays(3));
        changeRequest.setProposed_start(LocalTime.of(15, 0));
        changeRequest.setProposed_end(LocalTime.of(16, 0));
        changeRequest.setExpires_at(LocalDateTime.now().plusHours(1));

        when(userRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(bcrRepo.findById(changeRequest.getId())).thenReturn(Optional.of(changeRequest));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bcrRepo.save(any(BookingChangeRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updated = bcrService.acceptProposal(changeRequest.getId(), customer.getEmail());

        assertEquals(BookingStatus.CONFIRMED, updated.getStatus());
        assertEquals(changeRequest.getProposed_date(), updated.getDate());
        assertEquals(BookingStatus.CONFIRMED, changeRequest.getStatus());
    }
}
