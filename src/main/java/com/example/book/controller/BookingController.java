package com.example.book.controller;

import com.example.book.dto.BookingDto;
import com.example.book.dto.ServiceDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.Service;
import com.example.book.response.BookingResponse;
import com.example.book.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@PreAuthorize("hasAuthority('ROLE_USER')")
@CrossOrigin
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookService(@RequestBody BookingDto input, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bookingService.createBooking(input, email);
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable Long id , @AuthenticationPrincipal(expression = "username") String email){

        Booking booking = bookingService.manageBooking(id, email, "accept");

        return ResponseEntity.ok(BookingResponse.fromBooking(booking,email));
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineBooking(@PathVariable Long id, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bookingService.manageBooking(id,email,"decline");
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal(expression = "username")String email){
        Booking booking = bookingService.manageBooking(id,email,"cancel");
        return ResponseEntity.ok(BookingResponse.fromBooking(booking,email));
    }

}
