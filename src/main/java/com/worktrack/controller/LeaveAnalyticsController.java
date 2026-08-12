package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.response.LeaveAnalyticsResponse;
import com.worktrack.service.LeaveAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics/leaves")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class LeaveAnalyticsController {

    private final LeaveAnalyticsService leaveAnalyticsService;

    @GetMapping
    public ApiResponse<LeaveAnalyticsResponse> getLeaveAnalytics(
            @RequestParam Long companyId) {

        LeaveAnalyticsResponse response =
                leaveAnalyticsService.getLeaveAnalytics(companyId);

        return ApiResponse.<LeaveAnalyticsResponse>builder()
                .success(true)
                .message("Leave analytics retrieved successfully")
                .data(response)
                .build();
    }
}