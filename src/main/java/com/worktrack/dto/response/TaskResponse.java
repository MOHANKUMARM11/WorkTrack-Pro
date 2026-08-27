package com.worktrack.dto.response;

import com.worktrack.constants.TaskPriority;
import com.worktrack.constants.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Builder.Default
    private List<TaskAssignmentResponse> assignments = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}