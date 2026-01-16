package com.example.book.controller;

import com.example.book.dto.*;
import com.example.book.exception.AccountNotVerifiedException;
import com.example.book.model.TokenPurpose;
import com.example.book.model.User;
import com.example.book.repository.UserRepository;
import com.example.book.response.LoginResponse;
import com.example.book.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "User registration, login, and verification")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate user and return JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up", description = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterUserDto registerUserDto){
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(UserDto.from(registeredUser));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify account", description = "Verify a user with a code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verified"),
            @ApiResponse(responseCode = "400", description = "Invalid/expired code", content = @Content)
    })
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto){
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account Verified Successfully");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    @Operation(summary = "Resend verification", description = "Resend verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resent"),
            @ApiResponse(responseCode = "400", description = "Request failed", content = @Content)
    })
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
    @Operation(summary = "Change password", description = "Change password for the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto input, @AuthenticationPrincipal(expression = "username") String email){
        authenticationService.changePassword(input, email);
        return ResponseEntity.ok().body("Password Changed");
    }

    @PostMapping("/forgot")
    @Operation(summary = "Forgot password", description = "Send reset instructions if account exists")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Instruction sent (or noop)"),
            @ApiResponse(responseCode = "400", description = "Request failed", content = @Content)
    })
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
    @Operation(summary = "Reset password", description = "Reset password using a token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password reset"),
            @ApiResponse(responseCode = "400", description = "Invalid token/email", content = @Content)
    })
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
