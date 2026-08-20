package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserId(
            Long userId,
            Pageable pageable
    );

    long countByUserIdAndReadStatus(
            Long userId,
            Boolean readStatus
    );

}
