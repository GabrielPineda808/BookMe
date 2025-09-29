package com.example.book.service;

import com.example.book.dto.BookingDto;
import com.example.book.dto.ChangePasswordDto;
import com.example.book.dto.UserDto;
import com.example.book.exception.PasswordsDoNotMatchException;
import com.example.book.exception.UserNotFoundException;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User updateUser(UserDto input, String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));

        user.setFirst_name(input.getFirst_name());
        user.setLast_name(input.getLast_name());
        user.setLocation(input.getLocation());

        return userRepository.save(user);

    }

    public User findUserByUsername(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));
    }

    public void changePassword(ChangePasswordDto input, String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new PasswordsDoNotMatchException("PASSWORDS_DO_NOT_MATCH");
        }

        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        userRepository.save(user);
    }

    public void disableUser(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));

        user.setEnabled(false);

        userRepository.save(user);

    }

    public void updatePhone(String email, String phone) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));
        if(user.getPhone().equals(phone)){
            throw new RuntimeException("Phone Number Already In Use");
        }
        user.setPhone(phone);
        userRepository.save(user);
    }

}
