package com.example.book.controller;

import com.example.book.dto.BookingDto;
import com.example.book.dto.ServiceDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.Service;
import com.example.book.response.BookingResponse;
import com.example.book.response.ServiceResponse;
import com.example.book.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookService(@Valid @RequestBody BookingDto input, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bookingService.createBooking(input, email);
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    @PutMapping("/{id}/service-accept")
    public ResponseEntity<?> acceptBooking(@PathVariable Long id , @AuthenticationPrincipal(expression = "username") String email){

        Booking booking = bookingService.manageBooking(id, email, "accept");

        return ResponseEntity.ok(BookingResponse.fromBooking(booking,email));
    }

    @PutMapping("/{id}/service-decline")
    public ResponseEntity<?> declineBooking(@PathVariable Long id, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bookingService.manageBooking(id,email,"decline");
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    @PutMapping("/{id}/cancel-booking")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal(expression = "username")String email){
        Booking booking = bookingService.manageBooking(id,email,"cancel");
        return ResponseEntity.ok(BookingResponse.fromBooking(booking,email));
    }

    //need to implement more endpoints like getting my bookngs and search funcitonality for my bookings like in service controller

    //my bookings
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal(expression = "username") String email){
        List<Booking> bookings = bookingService.getBookingsByOwner(email);
        return ResponseEntity.ok(bookings.stream().map((booking)->BookingResponse.fromBooking(booking,email)).toList());
    }
}
