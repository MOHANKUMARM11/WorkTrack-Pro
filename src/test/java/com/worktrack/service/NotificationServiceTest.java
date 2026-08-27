package com.worktrack.service;

import com.worktrack.dto.response.NotificationResponse;
import com.worktrack.entity.Notification;
import com.worktrack.entity.User;
import com.worktrack.repository.NotificationPreferenceRepository;
import com.worktrack.repository.NotificationRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.serviceImpl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User sampleUser;
    private Notification sampleNotification;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().email("john@acme.com").build();
        ReflectionTestUtils.setField(sampleUser, "id", 100L);

        sampleNotification = Notification.builder()
                .title("Leave Approved")
                .message("Your leave request has been approved.")
                .type("LEAVE_APPROVAL")
                .isRead(false)
                .user(sampleUser)
                .build();
        ReflectionTestUtils.setField(sampleNotification, "id", 500L);
    }

    @Test
    @DisplayName("Should return user notifications")
    void getUserNotifications_Success() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(List.of(sampleNotification));

        List<NotificationResponse> responses = notificationService.getUserNotifications(100L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("Leave Approved");
    }

    @Test
    @DisplayName("Should mark notification as read for user")
    void markAsRead_Success() {
        when(notificationRepository.findById(500L)).thenReturn(Optional.of(sampleNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(sampleNotification);

        NotificationResponse response = notificationService.markAsRead(500L, 100L);

        assertThat(response).isNotNull();
        assertThat(sampleNotification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("Should return unread count")
    void getUnreadCount_Success() {
        when(notificationRepository.countByUserIdAndIsReadFalse(100L)).thenReturn(3L);

        long count = notificationService.getUnreadCount(100L);

        assertThat(count).isEqualTo(3L);
    }
}
