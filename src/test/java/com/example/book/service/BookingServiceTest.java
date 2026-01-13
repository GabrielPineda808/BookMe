package com.example.book.service;

import com.example.book.dto.BookingDto;
import com.example.book.exception.BookingOverlapException;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
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
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ServiceRepository serviceRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingService bookingService;

    private User user;
    private Service service;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("owner@example.com");
        user.setRole(Role.USER);
        user.setEnabled(true);

        service = new Service();
        service.setId(10L);
        service.setUser(user);
        service.setInterval(60);
        service.setOpen(LocalTime.of(9, 0));
        service.setClose(LocalTime.of(17, 0));
        service.setEnabled(true);

        bookingDto = new BookingDto();
        bookingDto.setServiceId(service.getId());
        bookingDto.setStart(LocalTime.of(10, 0));
        bookingDto.setEnd(LocalTime.of(11, 0));
        bookingDto.setDate(LocalDate.now().plusDays(1));
        bookingDto.setNotes("First booking");
    }

    @Test
    void createBooking_persistsBookingWhenValid() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
        when(bookingRepository.existsOverlapping(any(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking created = bookingService.createBooking(bookingDto, user.getEmail());

        assertEquals(BookingStatus.PENDING, created.getStatus());
        assertEquals(service, created.getService());
        assertEquals(user, created.getUser());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_throwsWhenOverlapDetected() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
        when(bookingRepository.existsOverlapping(any(), any(), any(), any())).thenReturn(true);

        assertThrows(BookingOverlapException.class,
                () -> bookingService.createBooking(bookingDto, user.getEmail()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void manageBooking_cancelsWhenRequestedByCustomer() {
        User customer = new User();
        customer.setId(2L);
        customer.setEmail("customer@example.com");
        customer.setRole(Role.USER);
        customer.setEnabled(true);

        Booking booking = new Booking();
        booking.setId(55L);
        booking.setUser(customer);
        booking.setService(service);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDate(LocalDate.now().plusDays(2));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));
        when(bookingRepository.existsOverlapping(any(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updated = bookingService.manageBooking(booking.getId(), customer.getEmail(), "cancel");

        assertEquals(BookingStatus.CANCELLED, updated.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void manageBooking_confirmsWhenServiceOwnerAccepts() {
        Booking booking = new Booking();
        booking.setId(77L);
        booking.setUser(user);
        booking.setService(service);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDate(LocalDate.now().plusDays(2));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bookingRepository.existsOverlapping(any(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking updated = bookingService.manageBooking(booking.getId(), user.getEmail(), "accept");

        assertEquals(BookingStatus.CONFIRMED, updated.getStatus());
        verify(bookingRepository).save(booking);
    }
}
