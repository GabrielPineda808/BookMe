package com.example.book.dto;

import com.example.book.model.Booking;
import com.example.book.model.Location;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public LocalTime getOpen() {
        return open;
    }

    public void setOpen(LocalTime open) {
        this.open = open;
    }

    public LocalTime getClose() {
        return close;
    }

    public void setClose(LocalTime close) {
        this.close = close;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

}
