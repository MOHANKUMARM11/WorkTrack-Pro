package com.worktrack.service;

import com.worktrack.dto.response.LeaveAnalyticsResponse;

public interface LeaveAnalyticsService {

    LeaveAnalyticsResponse getLeaveAnalytics(Long companyId);
}