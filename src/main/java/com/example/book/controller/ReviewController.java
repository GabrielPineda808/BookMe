package com.example.book.controller;

import com.example.book.dto.ReviewDto;
import com.example.book.model.Review;
import com.example.book.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@PreAuthorize("hasAuthority('ROLE_USER')")
@CrossOrigin
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review")
    public ResponseEntity<?> createReview(@RequestBody ReviewDto input, @AuthenticationPrincipal(expression = "username") String email){
        Review review = reviewService.createReview(input, email);
        return ResponseEntity.ok(review);
    }
}
