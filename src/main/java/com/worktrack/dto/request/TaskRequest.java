package com.worktrack.dto.request;

import com.worktrack.constants.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(min = 3, max = 100,
            message = "Task title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000,
            message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Task priority is required")
    private TaskPriority priority;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be a future date")
    private LocalDate dueDate;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Company ID is required")
    private Long companyId;

}