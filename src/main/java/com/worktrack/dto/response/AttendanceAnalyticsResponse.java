package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceAnalyticsResponse {

    private Long totalRecords;

    private Long presentCount;

    private Long absentCount;

    private Long lateCount;

    private Long halfDayCount;

    private Double averageWorkingHours;
}