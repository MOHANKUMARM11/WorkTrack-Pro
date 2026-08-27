package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {

    private Long id;

    private Long employeeId;

    private String employeeName;

    private Long leaveTypeId;

    private String leaveTypeName;

    private String leaveTypeCode;

    private Integer year;

    private Double allocatedDays;

    private Double usedDays;

    private Double pendingDays;

    private Double remainingDays;
}
