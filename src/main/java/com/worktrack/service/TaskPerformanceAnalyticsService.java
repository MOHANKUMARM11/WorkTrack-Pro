package com.worktrack.service;

import com.worktrack.dto.response.TaskPerformanceAnalyticsResponse;

public interface TaskPerformanceAnalyticsService {

    TaskPerformanceAnalyticsResponse getTaskPerformanceAnalytics(
            Long companyId);
}