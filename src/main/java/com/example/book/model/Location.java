package com.example.book.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Table(name="location")
public class Location {

    private String city;

    private String country;

    private String address;

    private String area_code;

    private String state;

    public Location() {
    }

    public Location(String city, String country, String address, String area_code,String state) {
        this.city = city;
        this.country = country;
        this.address = address;
        this.area_code = area_code;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
