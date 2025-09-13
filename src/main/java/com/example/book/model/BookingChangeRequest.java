package com.example.book.model;

import com.example.book.audit.AuditableBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Getter
@Setter
@Table(name="booking_change_request")
@EntityListeners(AuditingEntityListener.class)
public class BookingChangeRequest extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "current_booking_date", nullable = false)
    private LocalDate current_date;

    @Column(name = "current_start", nullable = false)
    private LocalTime current_start;

    @Column(name = "current_end", nullable = false)
    private LocalTime current_end; // (snapshots from the booking at the time of request)

    @Column(name = "proposed_date", nullable = false)
    private LocalDate proposed_date;
    @Column(name = "proposed_start", nullable = false)
    private LocalTime proposed_start;
    @Column(name = "proposed_end", nullable = false)
    private LocalTime proposed_end;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status; // ENUM: PENDING, APPROVED, DECLINED, CANCELLED, EXPIRED

    @Column(name = "reason")
    private String reason;// (nullable text from requester) why i want to change it
    @Column(name = "response_reason")
    private String response_reason;// (nullable text from responder) why i did or did not accept the proposed time

    @Column(name = "expires_at")
    private LocalDateTime expires_at;// (nullable; e.g., auto-expire after N hours)

    public BookingChangeRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(LocalDate current_date) {
        this.current_date = current_date;
    }

    public LocalTime getCurrent_start() {
        return current_start;
    }

    public void setCurrent_start(LocalTime current_start) {
        this.current_start = current_start;
    }

    public LocalTime getCurrent_end() {
        return current_end;
    }

    public void setCurrent_end(LocalTime current_end) {
        this.current_end = current_end;
    }

    public LocalDate getProposed_date() {
        return proposed_date;
    }

    public void setProposed_date(LocalDate proposed_date) {
        this.proposed_date = proposed_date;
    }

    public LocalTime getProposed_start() {
        return proposed_start;
    }

    public void setProposed_start(LocalTime proposed_start) {
        this.proposed_start = proposed_start;
    }

    public LocalTime getProposed_end() {
        return proposed_end;
    }

    public void setProposed_end(LocalTime proposed_end) {
        this.proposed_end = proposed_end;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
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

    public LocalDateTime getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(LocalDateTime expires_at) {
        this.expires_at = expires_at;
    }
}
