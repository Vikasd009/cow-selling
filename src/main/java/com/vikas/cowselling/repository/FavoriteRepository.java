package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndCowId(
            Long userId,
            Long cowId
    );

    Page<Favorite> findByUserId(
            Long userId,
            Pageable pageable
    );

    boolean existsByUserIdAndCowId(
            Long userId,
            Long cowId
    );
}

