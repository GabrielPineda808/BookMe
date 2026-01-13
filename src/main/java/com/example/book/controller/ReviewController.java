package com.example.book.controller;

import com.example.book.dto.ReviewDto;
import com.example.book.model.Review;
import com.example.book.response.ReviewResponse;
import com.example.book.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/review")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto input, @AuthenticationPrincipal(expression = "username") String email){
        Review review = reviewService.createReview(input, email);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @PutMapping("/{id}/update-review")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDto input, @AuthenticationPrincipal(expression = "username") String email){
        Review review = reviewService.updateReview(id, input, email);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @DeleteMapping("/{id}/delete-review")
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @AuthenticationPrincipal(expression = "username") String email){
        return reviewService.deleteReview(id, email);
    }
}
