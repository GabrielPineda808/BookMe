package com.example.book.dto;

import com.example.book.model.Booking;
import com.example.book.model.Location;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;


@Getter
@Setter
public class ServiceDto {

    @NotBlank(message = "Service handle is required")
    @Size(min = 3, max = 10, message = "Handle must be between 3 and 10 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Handle can only contain letters, numbers, hyphens, and underscores")
    private String handle;

    @NotBlank(message = "Service name is required")
    @Size(min = 2, max = 50, message = "Service name must be between 2 and 50 characters")
    private String service_name;

    private LocationDto location;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String desc;

    @NotNull(message = "Opening time is required")
    private LocalTime open;

    @NotNull(message = "Closing time is required")
    private LocalTime close;

    @Min(value = 5, message = "Interval must be at least 5 minutes")
    @Max(value = 240, message = "Interval cannot exceed 240 minutes")
    private int interval;

}
