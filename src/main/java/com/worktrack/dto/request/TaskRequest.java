package com.worktrack.dto.request;

import com.worktrack.constants.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TaskPriority priority;

    @NotNull
    @Future
    private LocalDate dueDate;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long companyId;
}