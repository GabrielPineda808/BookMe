package com.example.book.service;

import com.example.book.dto.*;
import com.example.book.exception.*;
import com.example.book.model.Role;
import com.example.book.model.TokenPurpose;
import com.example.book.model.User;
import com.example.book.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.Token;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    @Transactional
    public User signup(RegisterUserDto input){
        Optional<User> existingOptional = userRepository.findByEmail(input.getEmail());
        if (existingOptional.isPresent()) {
            User existing = existingOptional.get();
            existing.setVerification_code(generateVerificationCode());
            existing.setVerification_expiration(LocalDateTime.now().plusMinutes(5));
            sendVerificationEmail(existing);
            return existing;
        }
        User user = new User();
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEmail(input.getEmail());
        user.setFirst_name(input.getFirstName());
        user.setLast_name(input.getLastName());
        user.setLocation(null);
        user.setRole(Role.USER);
        user.setVerification_code(generateVerificationCode());
        user.setVerification_expiration(LocalDateTime.now().plusMinutes(5));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }


    public void requestEmailChange(String currentEmail, String newEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // Store new email temporarily in user entity or a separate field
        user.setEnabled(false);
        user.setEmail(newEmail);
        user.setVerification_code(generateVerificationCode());
        user.setVerification_expiration(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        sendVerificationEmail(user); // send to pendingEmail
    }

    public void verifyUser(VerifyUserDto input){
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getVerification_expiration().isBefore(LocalDateTime.now())){
                throw new VerificationCodeExpiredException("Verification Code Expired");
            }
            if(user.getVerification_code().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerification_code(null);
                user.setVerification_expiration(null);
                userRepository.save(user);
            }else{
                throw new InvalidVerificationCodeException("Invalid Verification Code");
            }
        }else{
            throw new UserNotFoundException("User Not Found");
        }
    }

    public void sendVerificationEmail(User user) {
        String subject = "Your verification code";
        String code = user.getVerification_code();

        if (code == null) throw new IllegalArgumentException("Missing verification code");

        String htmlTemplate = """
        <!doctype html>
        <html lang="en">
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width,initial-scale=1">
          <style>
            /* Small, safe media query for supported clients */
            @media only screen and (max-width:480px) {
              .container { width: 100% !important; padding: 18px !important; }
              .card { padding: 18px !important; border-radius: 10px !important; }
              .code { font-size: 34px !important; letter-spacing: 6px !important; }
              .lead { font-size: 14px !important; }
            }
            /* iOS link fix */
            a[x-apple-data-detectors] { color: inherit !important; text-decoration: none !important; }
          </style>
        </head>
        <body style="margin:0;background-color:#071022;font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; -webkit-font-smoothing:antialiased;">
          <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="min-width:320px;">
            <tr>
              <td align="center" style="padding:28px 12px;">
                <table role="presentation" class="container" width="520" cellpadding="0" cellspacing="0" style="width:520px;max-width:96%;background-color:#0b1220;border-radius:12px;box-shadow:0 8px 30px rgba(2,6,23,0.6);overflow:hidden;border:1px solid rgba(255,255,255,0.02);">
                  <tr>
                    <td class="card" style="padding:28px 32px;color:#e6eef8;">
                      <!-- Head -->
                      <div style="text-align:center;">
                        <div style="font-size:20px;font-weight:700;margin-bottom:6px;color:#e6eef8;">BookMe</div>
                        <div style="font-size:20px;font-weight:700;margin-bottom:6px;color:#e6eef8;">Verify your email</div>
                        <div style="font-size:13px;color:#90a9bf;margin-bottom:14px;">Enter the 6-digit code below to confirm your address.</div>
                      </div>

                      <!-- Message -->
                      <p style="margin:0 0 12px;color:#cfe6ff;font-size:14px;line-height:1.4;">
                        Use the code below to finish signing up. The code will expire in <strong style="color:#c0dff5;">15 minutes</strong>.
                      </p>

                      <!-- Code box -->
                      <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin:18px 0;">
                        <tr>
                          <td align="center">
                            <table role="presentation" cellpadding="0" cellspacing="0" style="display:inline-block;background:rgba(255,255,255,0.02);padding:14px 20px;border-radius:10px;border:1px solid rgba(255,255,255,0.03);">
                              <tr>
                                <td style="font-family:'Courier New', Courier, monospace;">
                                  <div class="code" style="font-size:40px;letter-spacing:8px;color:#e6f7ff;font-weight:800;">{{CODE}}</div>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>

                      <!-- Help / footer -->
                      <p style="margin:0 0 10px;color:#8ea9bf;font-size:12px;">If you didn't request this code, you can safely ignore this email.</p>

                      <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="margin-top:16px;border-top:1px solid rgba(255,255,255,0.01);padding-top:12px;">
                        <tr>
                          <td style="font-size:11px;color:#6f8ba1;text-align:center;">
                            Book • 123 Your Street, Your City
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """;
        String htmlMessage = htmlTemplate.replace("{{CODE}}", code);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new AccountNotVerifiedException("VERIFICATION_EMAIL_NOT_SENT");
        }
    }

    public String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public void resendVerification(ResendEmailRequestDto input){
        String email = input.getEmail().trim();
        if (email.length() >= 2 && email.startsWith("\"") && email.endsWith("\"")) {
            email = email.substring(1, email.length() - 1).trim();
        }
        System.out.println(email);
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(()-> new UserNotFoundException("USER_NOT_FOUND"));
        if(user.isEnabled()){
                throw new AccountAlreadyVerifiedException("Account Already Verified");
        }
        user.setVerification_code(generateVerificationCode());
        user.setVerification_expiration(LocalDateTime.now().plusMinutes(5));
        try{sendVerificationEmail(user);}catch(Exception e){ throw new AccountNotVerifiedException("VERIFICATION_EMAIL_NOT_SENT");}

        userRepository.save(user);
    }

    public User authenticate(LoginUserDto input){
        User user = userRepository.findByEmail(input.getEmail()).orElseThrow(()-> new UserNotFoundException("User Not Found"));//finding user via email method
        if(!user.isEnabled()){
            sendVerificationEmail(user);
            throw new AccountNotVerifiedException("Account Not Verified");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(),input.getPassword()));
        return user;
    }

    public void changePassword(ChangePasswordDto input, String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (!passwordEncoder.matches(input.getOldPassword(), user.getPassword())) {
            throw new PasswordsDoNotMatchException("PASSWORDS_DO_NOT_MATCH");
        }

        user.setPassword(passwordEncoder.encode(input.getNewPassword()));
        userRepository.save(user);
    }
}
