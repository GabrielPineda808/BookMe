package com.example.book.model;

import com.example.book.audit.AuditableBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

}
