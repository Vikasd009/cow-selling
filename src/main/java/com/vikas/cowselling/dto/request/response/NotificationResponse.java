package com.vikas.cowselling.dto.request.response;

import com.vikas.cowselling.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;

    private String message;

    private NotificationType type;

    private Boolean readStatus;

    private LocalDateTime createdAt;
}

