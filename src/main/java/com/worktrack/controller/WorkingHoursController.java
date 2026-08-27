package com.worktrack.controller;

import com.worktrack.dto.request.WorkingHoursRequest;
import com.worktrack.dto.response.WorkingDaysCalculationResponse;
import com.worktrack.dto.response.WorkingHoursResponse;
import com.worktrack.service.WorkingHoursService;
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
@RequestMapping("/api/v1/working-hours")
@RequiredArgsConstructor
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<WorkingHoursResponse> saveOrUpdateWorkingHours(@Valid @RequestBody WorkingHoursRequest request) {
        return new ResponseEntity<>(workingHoursService.saveOrUpdateWorkingHours(request), HttpStatus.CREATED);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<WorkingHoursResponse>> getWorkingHoursByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(workingHoursService.getWorkingHoursByCompanyId(companyId));
    }

    @GetMapping("/company/{companyId}/calculate")
    public ResponseEntity<WorkingDaysCalculationResponse> calculateWorkingDays(
            @PathVariable Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(workingHoursService.calculateWorkingDays(companyId, startDate, endDate));
    }
}
