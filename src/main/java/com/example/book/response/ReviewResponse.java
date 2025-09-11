package com.example.book.response;

public class ReviewResponse {
    private Long id;
    private Long serviceId;
    private Long reviewerId;
    private String reviewerDisplayName; // or avatar URL if you show it
    private int rating;                 // 1..5
    private String comment;
    private String createdAt;

    public static ReviewResponse from(com.example.book.model.Review r) {
        if (r == null) return null;

        ReviewResponse dto = new ReviewResponse();
        dto.setId(r.getId());
        dto.setReviewerId(r.getUser() != null ? r.getUser().getId() : null);
        dto.setServiceId(r.getService() != null ? r.getService().getId() : null);
        dto.setRating(Integer.parseInt(String.valueOf(r.getRating())));
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt().toString());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerDisplayName() {
        return reviewerDisplayName;
    }

    public void setReviewerDisplayName(String reviewerDisplayName) {
        this.reviewerDisplayName = reviewerDisplayName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
