package com.worktrack.service;

import com.worktrack.dto.response.DepartmentAnalyticsResponse;

import java.util.List;

public interface DepartmentAnalyticsService {

    List<DepartmentAnalyticsResponse> getDepartmentAnalytics(
            Long companyId);
}