package com.example.book.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UpdatePhoneDto {
    @NotBlank
    private String phone;
}
