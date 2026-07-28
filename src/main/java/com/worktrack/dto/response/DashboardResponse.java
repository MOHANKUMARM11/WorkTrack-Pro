package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    private Long totalCompanies;

    private Long totalEmployees;

    private Long presentToday;

    private Long absentToday;

    private Long pendingLeaves;

    private Long approvedLeaves;

    private Long totalTasks;

    private Long completedTasks;

    private Long pendingTasks;

    private Long totalPayrolls;

    private Long generatedPayrolls;
}