package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.response.AttendanceAnalyticsResponse;
import com.worktrack.service.AttendanceAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AttendanceAnalyticsController {

    private final AttendanceAnalyticsService attendanceAnalyticsService;

    @GetMapping
    public ApiResponse<AttendanceAnalyticsResponse> getAttendanceAnalytics(
            @RequestParam Long companyId) {

        AttendanceAnalyticsResponse response =
                attendanceAnalyticsService
                        .getAttendanceAnalytics(companyId);

        return ApiResponse.<AttendanceAnalyticsResponse>builder()
                .success(true)
                .message("Attendance analytics retrieved successfully")
                .data(response)
                .build();
    }
}
