package com.example.book.controller;

import com.example.book.dto.BookingDto;
import com.example.book.model.Booking;
import com.example.book.response.BookingResponse;
import com.example.book.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "Bookings", description = "Booking lifecycle management")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/book")
    @Operation(summary = "Create booking", description = "Book a service for a given date/time")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> bookService(@Valid @RequestBody BookingDto input, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bookingService.createBooking(input, email);
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    @PutMapping("/{id}/service-accept")
    @Operation(summary = "Accept booking", description = "Service owner accepts a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid state", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> acceptBooking(@PathVariable Long id , @AuthenticationPrincipal(expression = "username") String email){

        Booking booking = bookingService.manageBooking(id, email, "accept");

        return ResponseEntity.ok(BookingResponse.fromBooking(booking,email));
    }

    @PutMapping("/{id}/service-decline")
    @Operation(summary = "Decline booking", description = "Service owner declines a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking declined"),
            @ApiResponse(responseCode = "400", description = "Invalid state", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> declineBooking(@PathVariable Long id, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bookingService.manageBooking(id,email,"decline");
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    @PutMapping("/{id}/cancel-booking")
    @Operation(summary = "Cancel booking", description = "User cancels their booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid state", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal(expression = "username")String email){
        Booking booking = bookingService.manageBooking(id,email,"cancel");
        return ResponseEntity.ok(BookingResponse.fromBooking(booking,email));
    }

    //need to implement more endpoints like getting my bookngs and search funcitonality for my bookings like in service controller

    //my bookings
    @GetMapping("/my-bookings")
    @Operation(summary = "My bookings", description = "List bookings for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal(expression = "username") String email){
        List<Booking> bookings = bookingService.getBookingsByOwner(email);
        return ResponseEntity.ok(bookings.stream().map((booking)->BookingResponse.fromBooking(booking,email)).toList());
    }
}
