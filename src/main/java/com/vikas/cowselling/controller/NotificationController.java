package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.response.NotificationResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.service.NotificationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService =
                notificationService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>>
    getMyNotifications(
            Authentication authentication,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size
    ) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse>
    markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication
    ) {

        notificationService.markAllAsRead(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>>
    getUnreadCount(
            Authentication authentication
    ) {

        long count =
                notificationService.getUnreadCount(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                Map.of("unreadCount", count)
        );
    }

}

