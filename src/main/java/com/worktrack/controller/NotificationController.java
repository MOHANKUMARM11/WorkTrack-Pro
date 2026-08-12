package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.response.NotificationResponse;
import com.worktrack.entity.User;
import com.worktrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        List<NotificationResponse> notifications =
                notificationService.getUserNotifications(userId);

        return ApiResponse.<List<NotificationResponse>>builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(notifications)
                .build();
    }

    @PutMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        NotificationResponse response =
                notificationService.markAsRead(id, userId);

        return ApiResponse.<NotificationResponse>builder()
                .success(true)
                .message("Notification marked as read")
                .data(response)
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        long unreadCount =
                notificationService.getUnreadCount(userId);

        return ApiResponse.<Long>builder()
                .success(true)
                .message("Unread notification count retrieved successfully")
                .data(unreadCount)
                .build();
    }
}