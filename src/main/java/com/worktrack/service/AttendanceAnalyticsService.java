package com.worktrack.service;

import com.worktrack.dto.response.AttendanceAnalyticsResponse;

public interface AttendanceAnalyticsService {

    AttendanceAnalyticsResponse getAttendanceAnalytics(Long companyId);
}