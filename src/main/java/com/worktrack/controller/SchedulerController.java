package com.worktrack.controller;

import com.worktrack.scheduler.AttendanceAutoCheckoutScheduler;
import com.worktrack.scheduler.LeaveAccrualScheduler;
import com.worktrack.scheduler.SystemMaintenanceScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SchedulerController {

    private final AttendanceAutoCheckoutScheduler attendanceAutoCheckoutScheduler;
    private final LeaveAccrualScheduler leaveAccrualScheduler;
    private final SystemMaintenanceScheduler systemMaintenanceScheduler;

    @PostMapping("/trigger-auto-checkout")
    public ResponseEntity<Map<String, Object>> triggerAutoCheckout() {
        int processedCount = attendanceAutoCheckoutScheduler.processAutoCheckouts();
        return ResponseEntity.ok(Map.of(
                "job", "AttendanceAutoCheckout",
                "processedCount", processedCount,
                "status", "SUCCESS"
        ));
    }

    @PostMapping("/trigger-leave-accrual")
    public ResponseEntity<Map<String, Object>> triggerLeaveAccrual() {
        int processedCount = leaveAccrualScheduler.processMonthlyLeaveAccrual();
        return ResponseEntity.ok(Map.of(
                "job", "LeaveAccrual",
                "processedCount", processedCount,
                "status", "SUCCESS"
        ));
    }

    @PostMapping("/trigger-maintenance")
    public ResponseEntity<Map<String, Object>> triggerMaintenance() {
        int purgedCount = systemMaintenanceScheduler.processSystemMaintenance();
        return ResponseEntity.ok(Map.of(
                "job", "SystemMaintenance",
                "purgedCount", purgedCount,
                "status", "SUCCESS"
        ));
    }
}
