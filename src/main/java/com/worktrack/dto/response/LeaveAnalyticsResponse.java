package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveAnalyticsResponse {

    private Long totalLeaves;

    private Long pendingCount;

    private Long approvedCount;

    private Long rejectedCount;

    private Integer totalLeaveDays;
}