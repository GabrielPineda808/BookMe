package com.example.book.controller;


import com.example.book.dto.BookingChangeRequestDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.response.BookingResponse;
import com.example.book.service.BookingChangeRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/change-booking")
@PreAuthorize("hasAuthority('ROLE_USER')")
@CrossOrigin
public class BookingChangeRequestController {
    private final BookingChangeRequestService bcrs;

    public BookingChangeRequestController(BookingChangeRequestService bcrs) {
        this.bcrs = bcrs;
    }


    //user does not need to have a booking update request since we check for overlapping bookings the user cannot even
    //request for an already taken spot aka if im requesting as the user to move to a free slot its the same thing as if
    //i cancelled my current booking and booked the later time. The only time this would matter is if we handled refunds
    //as then cancleling within the hour may be a fine but thets not where we are right now. so for now only the service
    //can ask the user if they can book to a new time if they do not wANT  to str8 cancel on them as bad servie yk
    @PostMapping("/{id}/service-propose")
    public ResponseEntity<?> proposeChange(@PathVariable Long id, @RequestBody BookingChangeRequestDto input, @AuthenticationPrincipal(expression = "username") String email){
        BookingChangeRequest bookingChangeRequest = bcrs.proposeChangeAsService(id,input,email);
        return ResponseEntity.ok(BookingChangeRequestDto.from(bookingChangeRequest));
    }
    //need to create new proposal from the user or the service owner

    //accept proposal as the user or the service owner
    @PutMapping("/{id}/user-accept")
    public ResponseEntity<?> userAcceptProposal(@PathVariable Long id, @AuthenticationPrincipal(expression = "username") String email){
        Booking updatedBooking = bcrs.acceptProposal(id, email);
        return ResponseEntity.ok(BookingResponse.fromBooking(updatedBooking, email));
    }

    //decline proposal as the user
    @PutMapping("/{id}/user-decline")
    public ResponseEntity<?> userDeclineProposal(@PathVariable Long id, @RequestParam(required = false) String reason, @AuthenticationPrincipal(expression = "username") String email){
        Booking booking = bcrs.declineProposal(id, email, reason);
        return ResponseEntity.ok(BookingResponse.fromBooking(booking, email));
    }

    //get pending change requests for the user
    @GetMapping("/user-pending")
    public ResponseEntity<?> getUserPendingChangeRequests(@AuthenticationPrincipal(expression = "username") String email){
        return ResponseEntity.ok(bcrs.getPendingChangeRequestsForUser(email));
    }

    //get pending change requests for service owners
    @GetMapping("/service-pending")
    public ResponseEntity<?> getServicePendingChangeRequests(@AuthenticationPrincipal(expression = "username") String email){
        return ResponseEntity.ok(bcrs.getPendingChangeRequestsForServiceOwner(email));
    }

    //get change request by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getChangeRequest(@PathVariable Long id){
        return ResponseEntity.ok(bcrs.getChangeRequestById(id));
    }

    //expire old change requests
    @PostMapping("/expire-old")
    public ResponseEntity<?> expireOldChangeRequests(){
        bcrs.expireOldChangeRequests();
        return ResponseEntity.ok("Old change requests expired successfully");
    }

}
