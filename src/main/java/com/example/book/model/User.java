package com.example.book.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

    private String phone;

    @Column(nullable = false)
    private String created_at;

    @Column(nullable = false)
    private boolean enabled;

    private String verification_code;

    private LocalDateTime verification_expiration;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    public User() {
    }

    public User(String password, String email, String first_name, String last_name) {
        this.password = password;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public User(String email, String password, Role role, String first_name, String last_name, String phone, String created_at, boolean enabled, String verification_code, LocalDateTime verification_expiration, List<Booking> bookings, Location location, List<Review> reviews) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
        this.created_at = created_at;
        this.enabled = enabled;
        this.verification_code = verification_code;
        this.verification_expiration = verification_expiration;
        this.bookings = bookings;
        this.location = location;
        this.reviews = reviews;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() { //checks to see if account is on
        return enabled;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
