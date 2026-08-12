package com.worktrack.notification;

import com.worktrack.config.KafkaConfig;
import com.worktrack.notification.event.AttendanceLateEvent;
import com.worktrack.notification.event.LeaveApprovedEvent;
import com.worktrack.notification.event.NotificationEvent;
import com.worktrack.notification.event.TaskAssignedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void publishTaskAssigned(
            TaskAssignedEvent event) {

        NotificationEvent notificationEvent =
                new NotificationEvent(
                        "TaskAssigned",
                        event.getTaskId(),
                        event.getEmployeeId(),
                        "Task Assigned",
                        "A new task has been assigned to you: "
                                + event.getTaskTitle(),
                        "TASK_ASSIGNED");

        publish(notificationEvent);
    }

    public void publishAttendanceLate(
            AttendanceLateEvent event) {

        NotificationEvent notificationEvent =
                new NotificationEvent(
                        "AttendanceLate",
                        event.getAttendanceId(),
                        event.getEmployeeId(),
                        "Late Attendance",
                        "Your attendance was recorded as late for "
                                + event.getAttendanceDate() + ".",
                        "ATTENDANCE_LATE");

        publish(notificationEvent);
    }

    public void publishLeaveApproved(
            LeaveApprovedEvent event) {

        NotificationEvent notificationEvent =
                new NotificationEvent(
                        "LeaveApproved",
                        event.getLeaveId(),
                        event.getEmployeeId(),
                        "Leave Approved",
                        "Your leave request has been approved for "
                                + event.getTotalDays() + " day(s).",
                        "LEAVE_DECISION");

        publish(notificationEvent);
    }

    private void publish(
            NotificationEvent event) {

        kafkaTemplate.send(
                KafkaConfig.NOTIFICATION_TOPIC,
                String.valueOf(event.getEmployeeId()),
                event);
    }
}