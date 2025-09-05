package com.example.book.service;

import com.example.book.dto.LoginUserDto;
import com.example.book.dto.RegisterUserDto;
import com.example.book.dto.VerifyUserDto;
import com.example.book.model.Role;
import com.example.book.model.User;
import com.example.book.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        User user = new User(input.getFirst_name(),input.getLast_name(),input.getEmail(),passwordEncoder.encode(input.getPassword()));
        user.setRole(Role.USER);
        user.setVerification_code(generateVerificationCode());
        user.setVerification_expiration(LocalDateTime.now().plusMinutes(5));
        user.setEnabled(false);
        user.setCreated_at(LocalDateTime.now().format(dtf));
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public void verifyUser(VerifyUserDto input){
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getVerification_expiration().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification Code Expired");
            }
            if(user.getVerification_code().equals(input.getVerification_code())){
                user.setEnabled(true);
                user.setVerification_code(null);
                user.setVerification_expiration(null);
                userRepository.save(user);
            }else{
                throw new RuntimeException("Invalid Verification Code");
            }
        }else{
            throw new RuntimeException("User Not Found");
        }
    }

    public void sendVerificationEmail(User user){
        String subject = "Verify your account";
        String verificationCode = user.getVerification_code();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject,htmlMessage);
        }catch (MessagingException m){
            m.printStackTrace();
        }
    }

    public String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void resendVerification(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.isEnabled()){
                throw new RuntimeException("Account Already Verified");
            }
            user.setVerification_code(generateVerificationCode());
            user.setVerification_expiration(LocalDateTime.now().plusMinutes(5));
            sendVerificationEmail(user);
            userRepository.save(user);
        }else{
            throw new RuntimeException("User Not Found");
        }
    }

    public User authenticate(LoginUserDto input){
        User user = userRepository.findByEmail(input.getEmail()).orElseThrow(()-> new RuntimeException("User Not Found"));//finding user via email method
        if(!user.isEnabled()){
            throw new RuntimeException("Account Not Verified");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(),input.getPassword()));
        return user;
    }
}
