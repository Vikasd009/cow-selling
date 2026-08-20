package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.CreateReviewRequest;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.dto.request.response.ReviewResponse;
import com.vikas.cowselling.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@Validated
@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    @PostMapping("/cows/{cowId}/reviews")
    public ResponseEntity<ReviewResponse>
    createReview(
            @PathVariable Long cowId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                reviewService.createReview(
                        cowId,
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/sellers/{sellerId}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>>
    getSellerReviews(
            @PathVariable Long sellerId,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size
    ) {

        return ResponseEntity.ok(
                reviewService.getSellerReviews(
                        sellerId,
                        page,
                        size
                )
        );
    }

    @GetMapping("/sellers/{sellerId}/rating")
    public ResponseEntity<Map<String, Double>>
    getAverageRating(
            @PathVariable Long sellerId
    ) {

        return ResponseEntity.ok(
                Map.of(
                        "averageRating",
                        reviewService.getSellerAverageRating(
                                sellerId
                        )
                )
        );
    }
}

