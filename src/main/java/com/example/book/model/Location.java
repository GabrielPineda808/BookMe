package com.example.book.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String area_code;

    public Location() {
    }

    public Location(String city, String country, String address, String area_code) {
        this.city = city;
        this.country = country;
        this.address = address;
        this.area_code = area_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }
}
