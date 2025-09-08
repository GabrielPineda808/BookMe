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

        System.out.println("start booking");
        Booking booking = bookingService.createBooking(input, email);

        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setId(booking.getId());
        bookingResponse.setServiceId(booking.getService().getId());
        bookingResponse.setDate(booking.getDate());
        bookingResponse.setStart(booking.getStart());
        bookingResponse.setEnd(booking.getEnd());
        bookingResponse.setStatus(booking.getStatus());


        System.out.println("end booking");
        return ResponseEntity.ok(bookingResponse);
    }

}
