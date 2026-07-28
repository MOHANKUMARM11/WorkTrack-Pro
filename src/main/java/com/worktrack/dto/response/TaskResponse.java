package com.worktrack.dto.response;

import com.worktrack.constants.TaskPriority;
import com.worktrack.constants.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDate dueDate;

    private Long employeeId;

    private String employeeName;

    private Long companyId;

    private String companyName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}