package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardAnalyticsResponse {

    private Long totalEmployees;

    private Long totalTasks;

    private Long completedTasks;

    private Long pendingLeaves;

    private Long totalAttendanceRecords;

    private Double taskCompletionRate;

    private Double averageWorkingHours;
}