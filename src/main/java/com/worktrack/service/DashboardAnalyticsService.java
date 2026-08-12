package com.worktrack.service;

import com.worktrack.dto.response.DashboardAnalyticsResponse;

public interface DashboardAnalyticsService {

    DashboardAnalyticsResponse getDashboardAnalytics(Long companyId);
}