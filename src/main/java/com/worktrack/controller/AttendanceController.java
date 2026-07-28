package com.worktrack.controller;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceRequest;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        System.out.println("=== CREATE ATTENDANCE API CALLED ===");
        return new ResponseEntity<>(
                attendanceService.createAttendance(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {

        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {

        return ResponseEntity.ok(attendanceService.updateAttendance(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AttendanceResponse> updateAttendanceStatus(
            @PathVariable Long id,
            @RequestParam AttendanceStatus status) {

        return ResponseEntity.ok(
                attendanceService.updateAttendanceStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}