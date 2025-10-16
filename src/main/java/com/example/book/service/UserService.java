package com.example.book.service;

import com.example.book.dto.BookingDto;
import com.example.book.dto.ChangePasswordDto;
import com.example.book.dto.UserDto;
import com.example.book.dto.VerifyUserDto;
import com.example.book.exception.*;
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
import java.util.Random;

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

        send2FACode(user);
    }

    public String generate2FACode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit
        return String.valueOf(code);
    }

    public void send2FACode(User user) {
        user.setTwoFactorCode(generate2FACode());
        user.setTwoFactorExpiration(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // future sms sender
    }

    public void resend2FA(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.isPhoneEnabled()){
                throw new AccountAlreadyVerifiedException("Phone Number Verified");
            }
            send2FACode(user);
        }else{
            throw new UserNotFoundException("USER_NOT_FOUND");
        }
    }

    public void verifyUserPhone(String code, String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));
        if(user.getTwoFactorExpiration().isBefore(LocalDateTime.now())){
            throw new VerificationCodeExpiredException("Verification Code Expired");
        }
        if(user.getTwoFactorCode().equals(code)){
            user.setPhoneEnabled(true);
            user.setTwoFactorExpiration(null);
            user.setTwoFactorCode(null);
            userRepository.save(user);
        }else{
            throw new InvalidVerificationCodeException("Invalid Verification Code");
        }
    }

}
