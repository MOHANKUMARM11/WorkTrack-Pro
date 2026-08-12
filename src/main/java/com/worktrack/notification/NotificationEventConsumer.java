package com.worktrack.notification;

import com.worktrack.config.KafkaConfig;
import com.worktrack.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationDispatcher notificationDispatcher;

    @KafkaListener(
            topics = KafkaConfig.NOTIFICATION_TOPIC,
            groupId = "worktrack-notification-group")
    public void consume(NotificationEvent event) {

        notificationDispatcher.dispatch(
                event.getEmployeeId(),
                event.getTitle(),
                event.getMessage(),
                event.getNotificationType());
    }
}