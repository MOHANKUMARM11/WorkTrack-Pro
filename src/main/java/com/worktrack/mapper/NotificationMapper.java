package com.worktrack.mapper;

import com.worktrack.dto.response.NotificationResponse;
import com.worktrack.entity.Notification;

public class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(
            Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}