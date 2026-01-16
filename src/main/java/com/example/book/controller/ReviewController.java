package com.example.book.controller;

import com.example.book.dto.ReviewDto;
import com.example.book.model.Review;
import com.example.book.response.ReviewResponse;
import com.example.book.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reviews", description = "Service reviews")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/review")
    @Operation(summary = "Create review", description = "Create a review for a completed booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto input, @AuthenticationPrincipal(expression = "username") String email){
        Review review = reviewService.createReview(input, email);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @PutMapping("/{id}/update-review")
    @Operation(summary = "Update review", description = "Update an existing review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated"),
            @ApiResponse(responseCode = "400", description = "Update not allowed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDto input, @AuthenticationPrincipal(expression = "username") String email){
        Review review = reviewService.updateReview(id, input, email);
        return ResponseEntity.ok(ReviewResponse.from(review));
    }

    @DeleteMapping("/{id}/delete-review")
    @Operation(summary = "Delete review", description = "Delete a review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> deleteReview(@PathVariable Long id, @AuthenticationPrincipal(expression = "username") String email){
        return reviewService.deleteReview(id, email);
    }
}
