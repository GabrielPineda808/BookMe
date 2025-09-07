package com.example.book.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int serviceId;

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
    private Location location;

    @Column(name = "description")
    private String desc;

    @Column(nullable = false)
    private int interval;

    @Column(nullable = false)
    private String open;

    @Column(nullable = false)
    private String close;

    @OneToMany(mappedBy = "service")
    private List<Review> reviews;

    @OneToMany(mappedBy = "service")
    private List<Booking> bookings;

    public Service() {
    }

    public Service(User user, String handle, String service_name, Location location, List<Review> reviews, List<Booking> bookings) {
        this.user = user;
        this.handle = handle;
        this.service_name = service_name;
        this.location = location;
        this.reviews = reviews;
        this.bookings = bookings;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
