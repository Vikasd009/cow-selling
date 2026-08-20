package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.NotificationResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.NotificationType;

public interface NotificationService {

    void createNotification(
            User user,
            String message,
            NotificationType type
    );

    PageResponse<NotificationResponse> getMyNotifications(
            String userEmail,
            int page,
            int size
    );

    NotificationResponse markAsRead(
            Long notificationId,
            String userEmail
    );

    void markAllAsRead(
            String userEmail
    );

    long getUnreadCount(
            String userEmail
    );
}

