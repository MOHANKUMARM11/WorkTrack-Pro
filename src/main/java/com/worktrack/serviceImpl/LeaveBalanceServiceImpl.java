package com.worktrack.serviceImpl;

import com.worktrack.dto.response.LeaveBalanceResponse;
import com.worktrack.entity.Employee;
import com.worktrack.entity.LeaveBalance;
import com.worktrack.entity.LeaveType;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveBalanceRepository;
import com.worktrack.repository.LeaveTypeRepository;
import com.worktrack.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getEmployeeLeaveBalances(Long employeeId, Integer year) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException("Employee not found with id: " + employeeId);
        }
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        return leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, targetYear).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LeaveBalanceResponse allocateLeaveBalance(Long employeeId, Long leaveTypeId, Integer year, Double allocatedDays) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Leave type not found with id: " + leaveTypeId));

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        double initialAllocated = (allocatedDays != null) ? allocatedDays : leaveType.getDaysAllowedPerYear().doubleValue();

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveTypeId, targetYear)
                .orElseGet(() -> LeaveBalance.builder()
                        .employee(employee)
                        .leaveType(leaveType)
                        .year(targetYear)
                        .usedDays(0.0)
                        .pendingDays(0.0)
                        .build());

        balance.setAllocatedDays(initialAllocated);
        balance.setRemainingDays(initialAllocated - balance.getUsedDays());

        LeaveBalance saved = leaveBalanceRepository.save(balance);
        return mapToResponse(saved);
    }

    @Override
    public void deductApprovedLeave(Long employeeId, String leaveTypeCode, Integer year, Double daysToDeduct) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        LeaveType leaveType = leaveTypeRepository.findByCompanyIdAndCode(employee.getCompany().getId(), leaveTypeCode.toUpperCase())
                .orElse(null);

        if (leaveType == null) {
            return; // If specific leave type entity doesn't exist yet, gracefully bypass
        }

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employeeId, leaveType.getId(), targetYear)
                .orElseGet(() -> LeaveBalance.builder()
                        .employee(employee)
                        .leaveType(leaveType)
                        .year(targetYear)
                        .allocatedDays(leaveType.getDaysAllowedPerYear().doubleValue())
                        .usedDays(0.0)
                        .pendingDays(0.0)
                        .remainingDays(leaveType.getDaysAllowedPerYear().doubleValue())
                        .build());

        double newUsed = balance.getUsedDays() + daysToDeduct;
        double newRemaining = Math.max(0.0, balance.getAllocatedDays() - newUsed);

        balance.setUsedDays(newUsed);
        balance.setRemainingDays(newRemaining);

        leaveBalanceRepository.save(balance);
    }

    private LeaveBalanceResponse mapToResponse(LeaveBalance balance) {
        return LeaveBalanceResponse.builder()
                .id(balance.getId())
                .employeeId(balance.getEmployee().getId())
                .employeeName(balance.getEmployee().getFirstName() + " " + balance.getEmployee().getLastName())
                .leaveTypeId(balance.getLeaveType().getId())
                .leaveTypeName(balance.getLeaveType().getName())
                .leaveTypeCode(balance.getLeaveType().getCode())
                .year(balance.getYear())
                .allocatedDays(balance.getAllocatedDays())
                .usedDays(balance.getUsedDays())
                .pendingDays(balance.getPendingDays())
                .remainingDays(balance.getRemainingDays())
                .build();
    }
}
