package com.worktrack.controller;

import com.worktrack.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ReportController {

    private final ReportExportService reportExportService;

    @GetMapping("/attendance/csv")
    public ResponseEntity<byte[]> exportAttendanceReportCsv(
            @RequestParam Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        byte[] csvData = reportExportService.exportAttendanceReportCsv(companyId, startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/leave/csv")
    public ResponseEntity<byte[]> exportLeaveReportCsv(
            @RequestParam Long companyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        byte[] csvData = reportExportService.exportLeaveReportCsv(companyId, startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leave_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/payroll/csv")
    public ResponseEntity<byte[]> exportPayrollReportCsv(
            @RequestParam Long companyId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        byte[] csvData = reportExportService.exportPayrollReportCsv(companyId, year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
