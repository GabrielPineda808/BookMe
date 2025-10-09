package com.example.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequestDto {
    @NotBlank
    private String newEmail;

    public @NotBlank String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(@NotBlank String newEmail) {
        this.newEmail = newEmail;
    }
}
