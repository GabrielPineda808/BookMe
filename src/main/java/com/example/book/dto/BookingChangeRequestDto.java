package com.example.book.dto;

import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingChangeRequestDto {
    
    private Long id;
    
    @NotNull(message = "Booking ID is required")
    @Positive(message = "Booking ID must be positive")
    private Long booking_id;
    
    private Long user_id;
    
    private String current_date;
    private String current_start;
    private String current_end; // (snapshots from the booking at the time of request)
    
    @NotBlank(message = "Proposed date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Proposed date must be in YYYY-MM-DD format")
    private String proposed_date;
    
    @NotBlank(message = "Proposed start time is required")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Proposed start time must be in HH:MM format")
    private String proposed_start;
    
    @NotBlank(message = "Proposed end time is required")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Proposed end time must be in HH:MM format")
    private String proposed_end;
    
    private String status; // ENUM: PENDING, APPROVED, DECLINED, CANCELLED, EXPIRED
    
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;// (nullable text from requester) why i want to change it
    
    @Size(max = 500, message = "Response reason cannot exceed 500 characters")
    private String response_reason;// (nullable text from responder) why i did or did not accept the proposed time
    
    private String expires_at;

    public static BookingChangeRequestDto from(BookingChangeRequest input){

        // need to check if validation input is better like @NotNull or @NotEmpty
        BookingChangeRequestDto bcr = new BookingChangeRequestDto();
        if(input == null){
            return bcr;
        }
        bcr.setId(input.getId() == null ? null : input.getId());
        bcr.setBooking_id(input.getBooking().getId() == null ? null : input.getBooking().getId());
        bcr.setUser_id(input.getUser().getId() == null ? null : input.getUser().getId());
        bcr.setCurrent_date(input.getCurrent_date().toString() == null ? null : input.getCurrent_date().toString());
        bcr.setCurrent_start(input.getCurrent_start().toString() == null ? null : input.getCurrent_start().toString());
        bcr.setCurrent_end(input.getCurrent_end().toString() == null ? null : input.getCurrent_end().toString());
        bcr.setProposed_date(input.getProposed_date().toString() == null ? null : input.getProposed_date().toString());
        bcr.setProposed_start(input.getProposed_start().toString() == null ? null : input.getProposed_start().toString());
        bcr.setProposed_end(input.getProposed_end().toString() == null ? null : input.getProposed_end().toString());
        bcr.setStatus(input.getStatus().toString() == null ? null : input.getStatus().toString());
        bcr.setReason(input.getReason() == null ? null : input.getReason());
        bcr.setResponse_reason(input.getResponse_reason() == null ? null : input.getResponse_reason());
        bcr.setExpires_at(input.getExpires_at() != null ? input.getExpires_at().toString() : null);

        return bcr;

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getCurrent_start() {
        return current_start;
    }

    public void setCurrent_start(String current_start) {
        this.current_start = current_start;
    }

    public String getCurrent_end() {
        return current_end;
    }

    public void setCurrent_end(String current_end) {
        this.current_end = current_end;
    }

    public String getProposed_date() {
        return proposed_date;
    }

    public void setProposed_date(String proposed_date) {
        this.proposed_date = proposed_date;
    }

    public String getProposed_start() {
        return proposed_start;
    }

    public void setProposed_start(String proposed_start) {
        this.proposed_start = proposed_start;
    }

    public String getProposed_end() {
        return proposed_end;
    }

    public void setProposed_end(String proposed_end) {
        this.proposed_end = proposed_end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getResponse_reason() {
        return response_reason;
    }

    public void setResponse_reason(String response_reason) {
        this.response_reason = response_reason;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }
}
