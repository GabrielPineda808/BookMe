package com.example.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetDto {
    private String email;
    private String token;
    private String newPassword;
}

