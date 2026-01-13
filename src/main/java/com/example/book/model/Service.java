package com.example.book.model;

import com.example.book.audit.AuditableBase;
import com.example.book.dto.LocationDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="service")
public class Service extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String handle;

    @Column(nullable = false)
    private String service_name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "address",  column = @Column(name = "location_address")),
            @AttributeOverride(name = "city",    column = @Column(name = "location_city")),
            @AttributeOverride(name = "state",   column = @Column(name = "location_state")),
            @AttributeOverride(name = "area_code",     column = @Column(name = "location_area_code")),
            @AttributeOverride(name = "country", column = @Column(name = "location_country"))
    })
    private LocationDto location;

    @Column(name = "description")
    private String desc;

    @Column(nullable = false)
    @Min(5) @Max(240) private int interval;

    @Column(nullable = false)
    private LocalTime open;

    @Column(nullable = false)
    private LocalTime close;

    @OneToMany(mappedBy = "service")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "service")
    private List<Booking> bookings = new ArrayList<>();

    @Column(nullable = false)
    private boolean enabled;

}
