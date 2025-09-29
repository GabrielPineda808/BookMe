package com.example.book.controller;

import com.example.book.dto.ChangePasswordDto;
import com.example.book.dto.RegisterUserDto;
import com.example.book.dto.UserDto;
import com.example.book.model.Booking;
import com.example.book.model.User;
import com.example.book.service.UserService;
import jakarta.validation.Valid;
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
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


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

    //change password where the user passes in old password and then new pasword plus password confirmation then we change it
    //front end will have to send encoded password
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto input, @AuthenticationPrincipal(expression = "username") String email){
        userService.changePassword(input, email);
        return ResponseEntity.ok().body("Password Changed");
    }

    //delete user account aka disable it not actaully delete we update isEnablec
    @PutMapping("/delete-account")
    public ResponseEntity<?> disableUser(@AuthenticationPrincipal(expression = "username") String email){
        userService.disableUser(email);
        return ResponseEntity.noContent().build();
    }

    //change email
    //confirm email change
    //send verification

    //add phone number and 2fa

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
