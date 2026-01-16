package com.example.book.controller;

import com.example.book.dto.*;
import com.example.book.model.User;
import com.example.book.service.AuthenticationService;
import com.example.book.service.UserService;
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
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "Users", description = "User profile and account actions")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private UserService userService;
    private AuthenticationService authenticationService;


    @GetMapping("/profile")
    @Operation(summary = "My profile", description = "Get the current user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> myProfile(@AuthenticationPrincipal(expression = "username") String email){
        User user = userService.findUserByUsername(email);
        return ResponseEntity.ok(UserDto.from(user));
    }

    //see if this affects bookings
    @PostMapping("/update")
    @Operation(summary = "Update profile", description = "Update profile details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto input, @AuthenticationPrincipal(expression = "username") String email){
        User user = userService.updateUser(input, email);
        return ResponseEntity.ok(UserDto.from(user));
    }

    //delete user account aka disable it not actaully delete we update isEnablec
    @PutMapping("/delete-account")
    @Operation(summary = "Disable account", description = "Disable the current user account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account disabled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> disableUser(@AuthenticationPrincipal(expression = "username") String email){
        userService.disableUser(email);
        return ResponseEntity.noContent().build();
    }

    //change email
    @PutMapping("/change-email")
    @Operation(summary = "Change email", description = "Request an email change")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Change requested"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> requestEmailChange(
            @Valid @RequestBody ChangeEmailRequestDto input,
            @AuthenticationPrincipal(expression = "username") String email) {

        authenticationService.requestEmailChange(email, input.getNewEmail());

        return ResponseEntity.ok("Email Change Request Sent");
    }

    //add phone number and 2fa
    @PutMapping("/update-phone")
    @Operation(summary = "Update phone", description = "Request phone update/verification")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update requested"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> updatePhone(@Valid@RequestBody UpdatePhoneDto input, @AuthenticationPrincipal(expression = "username") String email){
        userService.updatePhone(input.getPhone(), email);
        return ResponseEntity.ok("Phone update request sent");
    }

    @PostMapping("/verify-phone")
    @Operation(summary = "Verify phone", description = "Verify phone using 2FA code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Phone verified"),
            @ApiResponse(responseCode = "400", description = "Invalid/expired code", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> verifyUserPhone(@RequestBody String code, @AuthenticationPrincipal(expression = "username") String email){
        try {
            userService.verifyUserPhone(code, email);
            return ResponseEntity.ok("Account Verified Successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-2FA")
    @Operation(summary = "Resend 2FA", description = "Resend phone verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Code resent"),
            @ApiResponse(responseCode = "400", description = "Request failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
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
