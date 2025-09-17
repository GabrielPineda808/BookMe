package com.example.book.service;

import com.example.book.dto.BookingDto;
import com.example.book.dto.UserDto;
import com.example.book.exception.UserNotFoundException;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BookingService bookingService;

    public UserService(UserRepository userRepository, BookingService bookingService) {
        this.userRepository = userRepository;
        this.bookingService = bookingService;
    }


    public List<Booking> getAllBookings(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            return user.getBookings();
        }else {
            throw new UserNotFoundException("USER_NOT_FOUND");
        }
    }

    public User updateUser(UserDto input, String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));

        user.setFirst_name(input.getFirst_name());
        user.setLast_name(input.getLast_name());
        user.setLocation(input.getLocation());

        return userRepository.save(user);

    }
}
