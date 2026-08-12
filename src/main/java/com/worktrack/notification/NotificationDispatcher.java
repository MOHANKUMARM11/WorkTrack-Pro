package com.worktrack.notification;

import com.worktrack.entity.Employee;
import com.worktrack.entity.Notification;
import com.worktrack.entity.User;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.NotificationRepository;
import com.worktrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void dispatch(
            Long employeeId,
            String title,
            String message,
            String type) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employee not found: "
                                                + employeeId));

        User user =
                userRepository.findByEmail(
                                employee.getEmail())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found for employee email: "
                                                + employee.getEmail()));

        Notification notification =
                Notification.builder()
                        .title(title)
                        .message(message)
                        .type(type)
                        .isRead(false)
                        .user(user)
                        .build();

        notificationRepository.save(notification);
    }
}