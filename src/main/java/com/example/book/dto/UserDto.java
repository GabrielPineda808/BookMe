package com.example.book.dto;

import com.example.book.model.*;
import com.example.book.response.BookingResponse;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name can only contain letters, spaces, hyphens, and apostrophes")
    private String first_name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name can only contain letters, spaces, hyphens, and apostrophes")
    private String last_name;

    @Valid
    private Location location;

    public static UserDto from(User user) {
        UserDto resp = new UserDto();
        resp.setId(user.getId());
        resp.setEmail(user.getEmail());
        resp.setFirst_name(user.getFirst_name());
        resp.setLast_name(user.getLast_name());
        resp.setLocation(user.getLocation());

        return resp;
    }
}
