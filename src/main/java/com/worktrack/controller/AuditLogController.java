package com.worktrack.controller;

import com.worktrack.dto.response.AuditLogResponse;
import com.worktrack.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByCompanyId(companyId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUserId(userId));
    }
}
