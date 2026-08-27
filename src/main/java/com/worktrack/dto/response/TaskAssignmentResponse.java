package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentResponse {

    private Long id;

    private Long taskId;

    private Long employeeId;

    private String employeeName;

    private LocalDateTime assignedAt;
}
