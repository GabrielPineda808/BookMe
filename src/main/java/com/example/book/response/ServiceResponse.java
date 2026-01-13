package com.example.book.response;

import com.example.book.dto.LocationDto;
import com.example.book.model.Service;
import com.example.book.model.Review;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
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
}
