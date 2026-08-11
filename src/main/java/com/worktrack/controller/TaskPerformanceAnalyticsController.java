package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.response.TaskPerformanceAnalyticsResponse;
import com.worktrack.service.TaskPerformanceAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class TaskPerformanceAnalyticsController {

    private final TaskPerformanceAnalyticsService taskPerformanceAnalyticsService;

    @GetMapping
    public ApiResponse<TaskPerformanceAnalyticsResponse> getTaskPerformanceAnalytics(
            @RequestParam Long companyId) {

        TaskPerformanceAnalyticsResponse response =
                taskPerformanceAnalyticsService
                        .getTaskPerformanceAnalytics(companyId);

        return ApiResponse.<TaskPerformanceAnalyticsResponse>builder()
                .success(true)
                .message("Task performance analytics retrieved successfully")
                .data(response)
                .build();
    }
}