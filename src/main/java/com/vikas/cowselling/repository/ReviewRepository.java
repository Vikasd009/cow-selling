package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    boolean existsByReviewerIdAndSellerIdAndCowId(
            Long reviewerId,
            Long sellerId,
            Long cowId
    );

    Page<Review> findBySellerId(
            Long sellerId,
            Pageable pageable
    );

    long countBySellerId(Long sellerId);

    @Query("""
        SELECT COALESCE(AVG(r.rating), 0)
        FROM Review r
        WHERE r.seller.id = :sellerId
        """)
    double getAverageRatingBySellerId(
            @Param("sellerId") Long sellerId
    );
}

