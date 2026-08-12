package com.worktrack.service;

import com.worktrack.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getUserNotifications(Long userId);

    NotificationResponse markAsRead(Long notificationId, Long userId);

    long getUnreadCount(Long userId);
}