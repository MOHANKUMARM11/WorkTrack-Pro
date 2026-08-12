package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.response.DashboardAnalyticsResponse;
import com.worktrack.service.DashboardAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService dashboardAnalyticsService;

    @GetMapping
    public ApiResponse<DashboardAnalyticsResponse> getDashboardAnalytics(
            @RequestParam Long companyId) {

        DashboardAnalyticsResponse response =
                dashboardAnalyticsService
                        .getDashboardAnalytics(companyId);

        return ApiResponse.<DashboardAnalyticsResponse>builder()
                .success(true)
                .message("Dashboard analytics retrieved successfully")
                .data(response)
                .build();
    }
}