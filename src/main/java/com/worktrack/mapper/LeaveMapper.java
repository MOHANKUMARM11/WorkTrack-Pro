package com.worktrack.mapper;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.dto.request.LeaveRequest;
import com.worktrack.dto.response.LeaveResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Leave;

import java.time.temporal.ChronoUnit;

public class LeaveMapper {

    public static Leave toEntity(
            LeaveRequest request,
            Employee employee,
            Company company) {

        long totalDays = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()) + 1;

        return Leave.builder()
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalDays((int) totalDays)
                .reason(request.getReason())
                .leaveType(request.getLeaveType())
                .status(LeaveStatus.PENDING)
                .employee(employee)
                .company(company)
                .build();
    }

    public static LeaveResponse toResponse(Leave leave) {

        return LeaveResponse.builder()
                .id(leave.getId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .totalDays(leave.getTotalDays())
                .reason(leave.getReason())
                .leaveType(leave.getLeaveType())
                .status(leave.getStatus())
                .employeeId(leave.getEmployee().getId())
                .employeeName(
                        leave.getEmployee().getFirstName() + " " +
                                leave.getEmployee().getLastName()
                )
                .companyId(leave.getCompany().getId())
                .companyName(leave.getCompany().getName())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }
}