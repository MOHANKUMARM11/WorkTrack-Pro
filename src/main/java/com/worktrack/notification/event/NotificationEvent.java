package com.worktrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String eventType;

    private Long entityId;

    private Long employeeId;

    private String title;

    private String message;

    private String notificationType;
}