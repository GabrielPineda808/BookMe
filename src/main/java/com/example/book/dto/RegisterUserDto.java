package com.example.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private String created_At;
}
