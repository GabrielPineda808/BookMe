package com.example.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    @NotBlank(message = "Old password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String newPassword;

    public @NotBlank(message = "Old password is required") String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(@NotBlank(message = "Old password is required") String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public @NotBlank(message = "New password is required") String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(@NotBlank(message = "New password is required") String newPassword) {
        this.newPassword = newPassword;
    }
}
