package com.example.book.dto;

import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
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
        bcr.setCurrent_date(input.getCurrent_date().toString());
        bcr.setCurrent_start(input.getCurrent_start().toString());
        bcr.setCurrent_end(input.getCurrent_end().toString());
        bcr.setProposed_date(input.getProposed_date().toString());
        bcr.setProposed_start(input.getProposed_start().toString());
        bcr.setProposed_end(input.getProposed_end().toString());
        bcr.setStatus(input.getStatus().toString() == null ? null : input.getStatus().toString());
        bcr.setReason(input.getReason() == null ? null : input.getReason());
        bcr.setResponse_reason(input.getResponse_reason() == null ? null : input.getResponse_reason());
        bcr.setExpires_at(input.getExpires_at() != null ? input.getExpires_at().toString() : null);

        return bcr;

    }
}
