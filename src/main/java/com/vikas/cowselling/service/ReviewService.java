package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateReviewRequest;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.dto.request.response.ReviewResponse;

public interface ReviewService {

    ReviewResponse createReview(
            Long cowId,
            CreateReviewRequest request,
            String reviewerEmail
    );

    PageResponse<ReviewResponse> getSellerReviews(
            Long sellerId,
            int page,
            int size
    );

    double getSellerAverageRating(
            Long sellerId
    );

}

