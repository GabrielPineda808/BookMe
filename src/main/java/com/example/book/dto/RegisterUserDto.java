package com.example.book.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String lastName;

    public @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") @Size(max = 255, message = "Email cannot exceed 255 characters") String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") @Size(max = 255, message = "Email cannot exceed 255 characters") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required") @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters") @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    ) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters") @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    ) String password) {
        this.password = password;
    }

    public @NotBlank(message = "First name is required") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "First name is required") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "Last name is required") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "Last name is required") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes") String lastName) {
        this.lastName = lastName;
    }
}
