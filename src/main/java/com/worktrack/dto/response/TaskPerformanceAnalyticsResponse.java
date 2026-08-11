package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskPerformanceAnalyticsResponse {

    private Long totalTasks;

    private Long todoCount;

    private Long inProgressCount;

    private Long completedCount;

    private Long overdueCount;

    private Double completionRate;
}