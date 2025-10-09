package com.example.book.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendEmailRequestDto {

    private String email;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


}
