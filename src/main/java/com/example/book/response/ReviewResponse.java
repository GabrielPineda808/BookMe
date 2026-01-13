package com.example.book.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private Long serviceId;
    private Long reviewerId;
    private String reviewerDisplayName;
    private int rating;
    private String comment;
    private String createdAt;
    private String updatedAt;

    public static ReviewResponse from(com.example.book.model.Review r) {
        if (r == null) return null;

        ReviewResponse dto = new ReviewResponse();
        dto.setId(r.getId());
        dto.setReviewerId(r.getUser() != null ? r.getUser().getId() : null);
        assert r.getUser() != null;
        dto.setReviewerDisplayName(r.getUser().getFirst_name() + " " + r.getUser().getLast_name());
        dto.setServiceId(r.getService() != null ? r.getService().getId() : null);
        dto.setRating(Integer.parseInt(String.valueOf(r.getRating())));
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt().toString());
        return dto;
    }

}
