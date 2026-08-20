package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.NotificationResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.entity.Notification;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.NotificationType;
import com.vikas.cowselling.exception.ForbiddenException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.NotificationRepository;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository =
                notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createNotification(
            User user,
            String message,
            NotificationType type
    ) {

        Notification notification =
                Notification.builder()
                        .user(user)
                        .message(message)
                        .type(type)
                        .readStatus(false)
                        .build();

        notificationRepository.save(notification);
    }

    @Override
    public PageResponse<NotificationResponse>
    getMyNotifications(
            String userEmail,
            int page,
            int size
    ) {

        User user = getUser(userEmail);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Notification> notificationPage =
                notificationRepository.findByUserId(
                        user.getId(),
                        pageable
                );

        List<NotificationResponse> responses =
                notificationPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<NotificationResponse>builder()
                .content(responses)
                .page(notificationPage.getNumber())
                .size(notificationPage.getSize())
                .totalElements(
                        notificationPage.getTotalElements()
                )
                .totalPages(
                        notificationPage.getTotalPages()
                )
                .first(notificationPage.isFirst())
                .last(notificationPage.isLast())
                .hasNext(notificationPage.hasNext())
                .hasPrevious(
                        notificationPage.hasPrevious()
                )
                .build();
    }

    @Override
    public NotificationResponse markAsRead(
            Long notificationId,
            String userEmail
    ) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                )
                        );

        if (!notification.getUser()
                .getEmail()
                .equals(userEmail)) {

            throw new ForbiddenException(
                    "You cannot access this notification"
            );
        }

        notification.setReadStatus(true);

        return mapToResponse(
                notificationRepository.save(notification)
        );
    }

    @Override
    public void markAllAsRead(
            String userEmail
    ) {

        User user = getUser(userEmail);

        Page<Notification> notifications =
                notificationRepository.findByUserId(
                        user.getId(),
                        Pageable.unpaged()
                );

        notifications.forEach(notification ->
                notification.setReadStatus(true)
        );

        notificationRepository.saveAll(
                notifications.getContent()
        );
    }

    @Override
    public long getUnreadCount(
            String userEmail
    ) {

        User user = getUser(userEmail);

        return notificationRepository
                .countByUserIdAndReadStatus(
                        user.getId(),
                        false
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

    private NotificationResponse mapToResponse(
            Notification notification
    ) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .readStatus(
                        notification.getReadStatus()
                )
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
