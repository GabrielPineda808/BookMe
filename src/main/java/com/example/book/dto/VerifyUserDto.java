package com.example.book.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits")
    private String verificationCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public @NotBlank(message = "Verification code is required") @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits") String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(@NotBlank(message = "Verification code is required") @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits") String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
