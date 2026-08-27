package com.worktrack.serviceImpl;

import com.worktrack.dto.response.AuditLogResponse;
import com.worktrack.entity.AuditLog;
import com.worktrack.entity.Company;
import com.worktrack.entity.User;
import com.worktrack.repository.AuditLogRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public AuditLogResponse recordAuditLog(String action, String entityName, String entityId, String details, String ipAddress) {
        User currentUser = null;
        Company currentCompany = null;
        String performedBy = "SYSTEM";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            currentUser = user;
            currentCompany = user.getCompany();
            performedBy = user.getEmail();
        }

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .performedBy(performedBy)
                .user(currentUser)
                .company(currentCompany)
                .details(details)
                .ipAddress(ipAddress)
                .build();

        return mapToResponse(auditLogRepository.save(auditLog));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByCompanyId(Long companyId) {
        return auditLogRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByUserId(Long userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .entityName(auditLog.getEntityName())
                .entityId(auditLog.getEntityId())
                .performedBy(auditLog.getPerformedBy())
                .userId(auditLog.getUser() != null ? auditLog.getUser().getId() : null)
                .companyId(auditLog.getCompany() != null ? auditLog.getCompany().getId() : null)
                .details(auditLog.getDetails())
                .ipAddress(auditLog.getIpAddress())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
