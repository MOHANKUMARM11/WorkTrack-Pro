package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.response.DepartmentAnalyticsResponse;
import com.worktrack.service.DepartmentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/departments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DepartmentAnalyticsController {

    private final DepartmentAnalyticsService departmentAnalyticsService;

    @GetMapping
    public ApiResponse<List<DepartmentAnalyticsResponse>>
    getDepartmentAnalytics(@RequestParam Long companyId) {

        List<DepartmentAnalyticsResponse> response =
                departmentAnalyticsService
                        .getDepartmentAnalytics(companyId);

        return ApiResponse.<List<DepartmentAnalyticsResponse>>builder()
                .success(true)
                .message("Department analytics retrieved successfully")
                .data(response)
                .build();
    }
}