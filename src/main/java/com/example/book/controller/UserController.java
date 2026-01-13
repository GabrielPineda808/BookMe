package com.example.book.controller;

import com.example.book.dto.*;
import com.example.book.model.Booking;
import com.example.book.model.User;
import com.example.book.service.AuthenticationService;
import com.example.book.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@CrossOrigin
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private AuthenticationService authenticationService;


    @GetMapping("/profile")
    public ResponseEntity<?> myProfile(@AuthenticationPrincipal(expression = "username") String email){
        User user = userService.findUserByUsername(email);
        return ResponseEntity.ok(UserDto.from(user));
    }

    //see if this affects bookings
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto input, @AuthenticationPrincipal(expression = "username") String email){
        User user = userService.updateUser(input, email);
        return ResponseEntity.ok(UserDto.from(user));
    }

    //delete user account aka disable it not actaully delete we update isEnablec
    @PutMapping("/delete-account")
    public ResponseEntity<?> disableUser(@AuthenticationPrincipal(expression = "username") String email){
        userService.disableUser(email);
        return ResponseEntity.noContent().build();
    }

    //change email
    @PutMapping("/change-email")
    public ResponseEntity<?> requestEmailChange(
            @Valid @RequestBody ChangeEmailRequestDto input,
            @AuthenticationPrincipal(expression = "username") String email) {

        authenticationService.requestEmailChange(email, input.getNewEmail());

        return ResponseEntity.ok("Email Change Request Sent");
    }

    //add phone number and 2fa
    @PutMapping("/update-phone")
    public ResponseEntity<?> updatePhone(@Valid@RequestBody UpdatePhoneDto input, @AuthenticationPrincipal(expression = "username") String email){
        userService.updatePhone(input.getPhone(), email);
        return ResponseEntity.ok("Phone update request sent");
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyUserPhone(@RequestBody String code, @AuthenticationPrincipal(expression = "username") String email){
        try {
            userService.verifyUserPhone(code, email);
            return ResponseEntity.ok("Account Verified Successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-2FA")
    public ResponseEntity<?> resend(@RequestParam String email){
        try {
            userService.resend2FA(email);
            return ResponseEntity.ok("Verification Code Resent");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //all my items
//    GET /bookings?status=&page=&size=
//
//    GET /reviews?page=&size=
//
//    GET /services?page=&size= (if user can be a provider)
//
//    GET /favorites
//
//    POST favorites/{serviceId}
//
//    DELETE favorites/{serviceId}






}
