package com.worktrack.controller;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceCheckInRequest;
import com.worktrack.dto.request.AttendanceCheckOutRequest;
import com.worktrack.dto.request.AttendanceCorrectionRequest;
import com.worktrack.dto.request.AttendanceRequest;
import com.worktrack.dto.request.BreakRequest;
import com.worktrack.dto.request.ManualCheckInApprovalRequest;
import com.worktrack.dto.response.AttendanceCheckInResponse;
import com.worktrack.dto.response.AttendanceHistoryResponse;
import com.worktrack.dto.response.AttendanceLogResponse;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.dto.response.AttendanceTodayResponse;
import com.worktrack.dto.response.BreakResponse;
import com.worktrack.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendance(
            @Valid @RequestBody AttendanceRequest request) {

        return new ResponseEntity<>(
                attendanceService.createAttendance(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {

        return ResponseEntity.ok(
                attendanceService.getAllAttendance()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {

        return ResponseEntity.ok(
                attendanceService.updateAttendance(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AttendanceResponse> updateAttendanceStatus(
            @PathVariable Long id,
            @RequestParam AttendanceStatus status) {

        return ResponseEntity.ok(
                attendanceService.updateAttendanceStatus(
                        id,
                        status
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteAttendance(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<AttendanceCheckInResponse> checkIn(
            @PathVariable Long employeeId,
            @Valid @RequestBody AttendanceCheckInRequest request) {

        return new ResponseEntity<>(
                attendanceService.checkIn(
                        employeeId,
                        request
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<AttendanceCheckInResponse> checkOut(
            @PathVariable Long employeeId,
            @Valid @RequestBody AttendanceCheckOutRequest request) {

        return ResponseEntity.ok(
                attendanceService.checkOut(
                        employeeId,
                        request
                )
        );
    }

    @GetMapping("/today/{employeeId}")
    public ResponseEntity<AttendanceTodayResponse> getTodayAttendance(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.getTodayAttendance(
                        employeeId
                )
        );
    }

    @GetMapping("/history/{employeeId}")
    public ResponseEntity<List<AttendanceHistoryResponse>> getAttendanceHistory(
            @PathVariable Long employeeId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) AttendanceStatus status) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceHistory(
                        employeeId,
                        startDate,
                        endDate,
                        status
                )
        );
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<AttendanceLogResponse>> getAttendanceLogs(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceLogs(id)
        );
    }

    @PostMapping("/break/start")
    public ResponseEntity<BreakResponse> startBreak(
            @RequestParam Long employeeId,
            @Valid @RequestBody BreakRequest request) {

        return new ResponseEntity<>(
                attendanceService.startBreak(
                        employeeId,
                        request
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/break/end")
    public ResponseEntity<BreakResponse> endBreak(
            @RequestParam Long employeeId) {

        return ResponseEntity.ok(
                attendanceService.endBreak(employeeId)
        );
    }

    @PutMapping("/{id}/correction")
    public ResponseEntity<AttendanceResponse> requestCorrection(
            @PathVariable Long id,
            @RequestParam Long employeeId,
            @Valid @RequestBody AttendanceCorrectionRequest request) {

        return ResponseEntity.ok(
                attendanceService.requestCorrection(
                        id,
                        employeeId,
                        request
                )
        );
    }

    @PostMapping("/{id}/manual-approval")
    public ResponseEntity<AttendanceResponse> approveManualCheckIn(
            @PathVariable Long id,
            @RequestParam Long managerId,
            @Valid @RequestBody ManualCheckInApprovalRequest request) {

        return ResponseEntity.ok(
                attendanceService.approveManualCheckIn(
                        id,
                        managerId,
                        request
                )
        );
    }
}