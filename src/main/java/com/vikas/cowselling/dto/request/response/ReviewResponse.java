package com.vikas.cowselling.dto.request.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {

    private Long id;

    private Integer rating;

    private String comment;

    private Long reviewerId;
    private String reviewerName;

    private Long sellerId;
    private String sellerName;

    private Long cowId;
    private String cowName;

    private LocalDateTime createdAt;

}

