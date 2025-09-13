package com.example.book.response;

import com.example.book.dto.LocationDto;
import com.example.book.model.Service;
import com.example.book.model.Review;


import java.time.LocalTime;

public class ServiceResponse {
    private Long id;
    private String handle;
    private String name;
    private String description;
    private LocationDto location;
    private String open;
    private String close;
    private int intervalMinutes;
    private Double averageRating;
    private Integer reviewCount;
    private String created_At;

    public static ServiceResponse fromService(Service service){
        if (service == null) return null;

        ServiceResponse dto = new ServiceResponse();

        double avgRating = service.getReviews().stream()
                .mapToLong(Review::getRating)
                .average()
                .orElse(0.0);  // fallback if no reviews



        dto.setLocation(service.getLocation());
        dto.setDescription(service.getDesc());
        dto.setHandle(service.getHandle());
        dto.setName(service.getService_name());
        dto.setClose(service.getClose().toString());
        dto.setId(service.getId());
        dto.setIntervalMinutes(service.getInterval());
        dto.setOpen(service.getOpen().toString());
        dto.setReviewCount(service.getReviews().size());
        dto.setAverageRating(avgRating);
        dto.setCreated_At(service.getCreatedAt().toString());

        return dto;
    }

    public String getCreated_At() {
        return created_At;
    }

    public void setCreated_At(String created_At) {
        this.created_At = created_At;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
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

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes) {
        this.intervalMinutes = intervalMinutes;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
