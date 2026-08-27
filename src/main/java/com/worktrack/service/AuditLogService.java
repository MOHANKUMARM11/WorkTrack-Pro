package com.worktrack.service;

import com.worktrack.dto.response.AuditLogResponse;

import java.util.List;

public interface AuditLogService {

    AuditLogResponse recordAuditLog(String action, String entityName, String entityId, String details, String ipAddress);

    List<AuditLogResponse> getAllAuditLogs();

    List<AuditLogResponse> getAuditLogsByCompanyId(Long companyId);

    List<AuditLogResponse> getAuditLogsByUserId(Long userId);
}
