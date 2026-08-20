package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateReviewRequest;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.dto.request.response.ReviewResponse;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.Review;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.ReviewRepository;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl
        implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CowRepository cowRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            CowRepository cowRepository,
            UserRepository userRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.cowRepository = cowRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponse createReview(
            Long cowId,
            CreateReviewRequest request,
            String reviewerEmail
    ) {

        User reviewer = getUser(reviewerEmail);

        Cow cow = cowRepository.findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found"
                        )
                );

        User seller = cow.getSeller();

        if (seller.getId().equals(reviewer.getId())) {
            throw new BadRequestException(
                    "You cannot review yourself"
            );
        }

        if (reviewRepository
                .existsByReviewerIdAndSellerIdAndCowId(
                        reviewer.getId(),
                        seller.getId(),
                        cowId
                )) {

            throw new BadRequestException(
                    "You have already reviewed this seller for this cow"
            );
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(
                        request.getComment() == null
                                ? null
                                : request.getComment().trim()
                )
                .reviewer(reviewer)
                .seller(seller)
                .cow(cow)
                .build();

        return mapToResponse(
                reviewRepository.save(review)
        );
    }

    @Override
    public PageResponse<ReviewResponse> getSellerReviews(
            Long sellerId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Review> reviewPage =
                reviewRepository.findBySellerId(
                        sellerId,
                        pageable
                );

        List<ReviewResponse> responses =
                reviewPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<ReviewResponse>builder()
                .content(responses)
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(
                        reviewPage.getTotalElements()
                )
                .totalPages(
                        reviewPage.getTotalPages()
                )
                .first(reviewPage.isFirst())
                .last(reviewPage.isLast())
                .hasNext(reviewPage.hasNext())
                .hasPrevious(
                        reviewPage.hasPrevious()
                )
                .build();
    }

    @Override
    public double getSellerAverageRating(
            Long sellerId
    ) {

        return reviewRepository
                .getAverageRatingBySellerId(
                        sellerId
                );
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private ReviewResponse mapToResponse(
            Review review
    ) {

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())

                .reviewerId(
                        review.getReviewer().getId()
                )
                .reviewerName(
                        review.getReviewer().getName()
                )

                .sellerId(
                        review.getSeller().getId()
                )
                .sellerName(
                        review.getSeller().getName()
                )

                .cowId(review.getCow().getId())
                .cowName(review.getCow().getName())

                .createdAt(review.getCreatedAt())
                .build();
    }

}

