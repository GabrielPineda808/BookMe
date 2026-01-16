package com.example.book.controller;

import com.example.book.dto.BookingChangeRequestDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.response.BookingResponse;
import com.example.book.service.BookingChangeRequestService;
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

@RestController
@RequestMapping("/change-booking")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "Booking Change Requests", description = "Propose and manage booking changes")
@SecurityRequirement(name = "bearerAuth")
public class BookingChangeRequestController {
    private final BookingChangeRequestService bcrs;

    // user does not need to have a booking update request since we check for
    // overlapping bookings the user cannot even
    // request for an already taken spot aka if im requesting as the user to move to
    // a free slot its the same thing as if
    // i cancelled my current booking and booked the later time. The only time this
    // would matter is if we handled refunds
    // as then cancleling within the hour may be a fine but thets not where we are
    // right now. so for now only the service
    // can ask the user if they can book to a new time if they do not wANT to str8
    // cancel on them as bad servie yk
    @PostMapping("/{id}/service-propose")
    @Operation(summary = "Propose change", description = "Service owner proposes a new time")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proposal created"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> proposeChange(@PathVariable Long id, @Valid @RequestBody BookingChangeRequestDto input,
            @AuthenticationPrincipal(expression = "username") String email) {
        BookingChangeRequest bookingChangeRequest = bcrs.proposeChangeAsService(id, input, email);
        return ResponseEntity.ok(BookingChangeRequestDto.from(bookingChangeRequest));
    }
    // need to create new proposal from the user or the service owner

    // accept proposal as the user or the service owner
    @PutMapping("/{id}/user-accept")
    @Operation(summary = "Accept proposal", description = "User accepts a proposed change")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proposal accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid state", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> userAcceptProposal(@PathVariable Long id,
            @AuthenticationPrincipal(expression = "username") String email) {
        Booking updatedBooking = bcrs.acceptProposal(id, email);
        return ResponseEntity.ok(BookingResponse.fromBooking(updatedBooking, email));
    }

    // decline proposal as the user
    @PutMapping("/{id}/user-decline")
    @Operation(summary = "Decline proposal", description = "User declines a proposed change")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proposal declined"),
            @ApiResponse(responseCode = "400", description = "Invalid state", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> userDeclineProposal(@PathVariable Long id, @RequestParam(required = false) String reason,
            @AuthenticationPrincipal(expression = "username") String email) {
        Booking booking = bcrs.declineProposal(id, email, reason);
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    // get pending change requests for the user
    @GetMapping("/user-pending")
    @Operation(summary = "Pending requests (user)", description = "List pending change requests for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getUserPendingChangeRequests(
            @AuthenticationPrincipal(expression = "username") String email) {
        return ResponseEntity.ok(bcrs.getPendingChangeRequestsForUser(email));
    }

    // get pending change requests for service owners
    @GetMapping("/service-pending")
    @Operation(summary = "Pending requests (service)", description = "List pending requests for service owners")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getServicePendingChangeRequests(
            @AuthenticationPrincipal(expression = "username") String email) {
        return ResponseEntity.ok(bcrs.getPendingChangeRequestsForServiceOwner(email));
    }

    // get change request by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get change request", description = "Fetch a change request by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request returned"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<?> getChangeRequest(@PathVariable Long id) {
        return ResponseEntity.ok(bcrs.getChangeRequestById(id));
    }

    // expire old change requests
    @PostMapping("/expire-old")
    @Operation(summary = "Expire old requests", description = "Expire outdated change requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expired"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> expireOldChangeRequests() {
        bcrs.expireOldChangeRequests();
        return ResponseEntity.ok("Old change requests expired successfully");
    }

}
