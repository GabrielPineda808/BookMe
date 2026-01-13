package com.example.book.dto;

import com.example.book.model.BookingStatus;
import com.example.book.model.Service;
import com.example.book.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingDto {

    @NotNull(message = "Service ID is required")
    @Positive(message = "Service ID must be positive")
    private Long serviceId;

    @NotNull(message = "Start time is required")
    private LocalTime start;

    @NotNull(message = "End time is required")
    private LocalTime end;

    @NotNull(message = "Booking date is required")
    @Future(message = "Booking date must be in the future")
    private LocalDate date;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        if (start == null || end == null) {
            return true; // @NotNull can handle validation if var is null
        }
        return end.isAfter(start);
    }
}
