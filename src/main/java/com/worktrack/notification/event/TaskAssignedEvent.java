package com.worktrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TaskAssignedEvent {

    private final Long taskId;
    private final Long employeeId;
    private final String taskTitle;
}