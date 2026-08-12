package com.worktrack.serviceImpl;

import com.worktrack.dto.response.NotificationResponse;
import com.worktrack.entity.Notification;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.mapper.NotificationMapper;
import com.worktrack.repository.NotificationRepository;
import com.worktrack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> getUserNotifications(
            Long userId) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(
            Long notificationId,
            Long userId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Notification not found");
        }

        notification.setIsRead(true);

        return NotificationMapper.toResponse(
                notificationRepository.save(notification));
    }

    @Override
    public long getUnreadCount(Long userId) {

        return notificationRepository
                .countByUserIdAndIsReadFalse(userId);
    }
}