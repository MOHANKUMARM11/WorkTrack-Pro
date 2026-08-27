package com.worktrack.controller;

import com.worktrack.dto.request.ShiftAssignmentRequest;
import com.worktrack.dto.request.ShiftRequest;
import com.worktrack.dto.response.ShiftAssignmentResponse;
import com.worktrack.dto.response.ShiftResponse;
import com.worktrack.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody ShiftRequest request) {
        return new ResponseEntity<>(shiftService.createShift(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponse> getShiftById(@PathVariable Long id) {
        return ResponseEntity.ok(shiftService.getShiftById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ShiftResponse>> getShiftsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(shiftService.getShiftsByCompanyId(companyId));
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ShiftAssignmentResponse> assignShiftToEmployee(@Valid @RequestBody ShiftAssignmentRequest request) {
        return new ResponseEntity<>(shiftService.assignShiftToEmployee(request), HttpStatus.CREATED);
    }

    @GetMapping("/assignments/employee/{employeeId}")
    public ResponseEntity<List<ShiftAssignmentResponse>> getAssignmentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(shiftService.getAssignmentsByEmployee(employeeId));
    }

    @GetMapping("/assignments/employee/{employeeId}/active")
    public ResponseEntity<ShiftAssignmentResponse> getActiveAssignmentOnDate(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(shiftService.getActiveAssignmentForEmployeeOnDate(employeeId, date));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
