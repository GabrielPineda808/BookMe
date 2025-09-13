package com.example.book.controller;

import com.example.book.dto.RegisterUserDto;
import com.example.book.dto.UserDto;
import com.example.book.model.Booking;
import com.example.book.model.User;
import com.example.book.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestBody String email){
        List<Booking> bookings = userService.getAllBookings(email);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto input, @AuthenticationPrincipal(expression = "username") String email){
        User user = userService.updateUser(input, email);
        return ResponseEntity.ok(UserDto.from(user));
    }


}
