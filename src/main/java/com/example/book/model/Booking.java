package com.example.book.model;

import com.example.book.audit.AuditableBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

@Entity
@Getter
@Setter
@Table(name="booking")
@EntityListeners(AuditingEntityListener.class)
public class Booking extends AuditableBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    // inside Booking entity
    @Enumerated(EnumType.STRING)
    @Column(name = "status")   // keep your existing name if different
    private BookingStatus status;

    @Column(name = "booking_start", nullable = false)
    private LocalTime start;

    @Column(name="booking_end", nullable = false)
    private LocalTime end;

    @Column(name = "notes")
    private String notes;

    @Column(name = "booking_date", nullable = false)
    private LocalDate date;


    public Booking() {
    }

    public Booking(LocalTime start, LocalTime end, LocalDate date) {
        this.start = start;
        this.date = date;
        this.end = end;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
