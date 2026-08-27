package com.worktrack.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignRequest {

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotEmpty(message = "At least one employee ID is required")
    private List<Long> employeeIds;
}
