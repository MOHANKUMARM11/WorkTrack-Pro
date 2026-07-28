package com.worktrack.dto.response;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.constants.LeaveType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class LeaveResponse {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private String reason;

    private LeaveType leaveType;

    private LeaveStatus status;

    private Long employeeId;

    private String employeeName;

    private Long companyId;

    private String companyName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}