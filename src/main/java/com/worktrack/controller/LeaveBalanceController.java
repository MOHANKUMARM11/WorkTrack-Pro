package com.worktrack.controller;

import com.worktrack.dto.response.LeaveBalanceResponse;
import com.worktrack.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceResponse>> getEmployeeLeaveBalances(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(leaveBalanceService.getEmployeeLeaveBalances(employeeId, year));
    }

    @PostMapping("/allocate")
    public ResponseEntity<LeaveBalanceResponse> allocateLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Double allocatedDays) {
        return ResponseEntity.ok(leaveBalanceService.allocateLeaveBalance(employeeId, leaveTypeId, year, allocatedDays));
    }
}
