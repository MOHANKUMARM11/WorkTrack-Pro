package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentAnalyticsResponse {

    private Long departmentId;

    private String departmentName;

    private Long employeeCount;

    private Long totalTasks;

    private Long completedTasks;

    private Long pendingLeaves;

    private Double taskCompletionRate;
}