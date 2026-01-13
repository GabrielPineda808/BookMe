package com.example.book.controller;

import com.example.book.dto.*;
import com.example.book.exception.AccountNotVerifiedException;
import com.example.book.model.TokenPurpose;
import com.example.book.model.User;
import com.example.book.repository.UserRepository;
import com.example.book.response.LoginResponse;
import com.example.book.service.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@RequestMapping("/auth")
@RestController
@CrossOrigin(origins ={"http://localhost:5173", "http://localhost:3000"})
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterUserDto registerUserDto){
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(UserDto.from(registeredUser));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto){
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account Verified Successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@RequestBody ResendEmailRequestDto input){
        try {
            authenticationService.resendVerification(input);
            return ResponseEntity.ok("Verification Code Resent");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //change password where the user passes in old password and then new pasword plus password confirmation then we change it
    //front end will have to send encoded password
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto input, @AuthenticationPrincipal(expression = "username") String email){
        authenticationService.changePassword(input, email);
        return ResponseEntity.ok().body("Password Changed");
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> input) {
        if (input.get("email") == null) return ResponseEntity.ok(Map.of("message","If an account exists, we've sent instructions."));

        String email = input.get("email").trim().toLowerCase();


        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            if(!user.isEnabled()){
                throw new AccountNotVerifiedException("ACCOUNT_NOT_ENABLED");
            }
            // create RESET token, TTL 15 minutes
            String rawToken = tokenService.createTokenFor(user, TokenPurpose.PASSWORD_RESET, Duration.ofMinutes(15));

            // Build a reset link for frontend
            String link = "http://localhost:5173/reset-password?token=" +
                    URLEncoder.encode(rawToken, StandardCharsets.UTF_8) +
                    "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);

            try {
                emailService.sendVerificationEmail(user.getEmail(), "Reset your password", link); // implement email method
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        // Always return neutral result to avoid user enumeration
        return ResponseEntity.ok(Map.of("message","If an account exists, we've sent reset instructions."));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token or email"));

        // validate token & mark used
        tokenService.validateAndConsumeToken(user, TokenPurpose.PASSWORD_RESET, dto.getToken());

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // optionally send a notification
        // emailService.sendPasswordChangeNotification(user.getEmail());

        return ResponseEntity.ok(Map.of("message","Password changed"));
    }
}
