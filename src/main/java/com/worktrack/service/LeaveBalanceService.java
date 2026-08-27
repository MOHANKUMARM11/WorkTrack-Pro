package com.worktrack.service;

import com.worktrack.dto.response.LeaveBalanceResponse;

import java.util.List;

public interface LeaveBalanceService {

    List<LeaveBalanceResponse> getEmployeeLeaveBalances(Long employeeId, Integer year);

    LeaveBalanceResponse allocateLeaveBalance(Long employeeId, Long leaveTypeId, Integer year, Double allocatedDays);

    void deductApprovedLeave(Long employeeId, String leaveTypeCode, Integer year, Double daysToDeduct);
}
