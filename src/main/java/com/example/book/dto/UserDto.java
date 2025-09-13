package com.example.book.dto;

import com.example.book.model.*;
import com.example.book.response.BookingResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserDto {

    private String first_name;

    private String last_name;

    private Location location;

    public static UserDto from(User user) {
        UserDto resp = new UserDto();

        resp.setFirst_name(user.getFirst_name());
        resp.setLast_name(user.getLast_name());
        resp.setLocation(user.getLocation());

        return resp;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
