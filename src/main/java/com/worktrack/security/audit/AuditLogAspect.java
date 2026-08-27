package com.worktrack.security.audit;

import com.worktrack.entity.AuditLog;
import com.worktrack.entity.User;
import com.worktrack.repository.AuditLogRepository;
import com.worktrack.security.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(value = "@annotation(auditable)", returning = "result")
    public void logAuditAction(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            User currentUser = null;
            String performedBy = "SYSTEM";

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                currentUser = user;
                performedBy = user.getEmail();
            }

            AuditLog logEntry = AuditLog.builder()
                    .action(auditable.action())
                    .entityName(auditable.entityName().isBlank() ? joinPoint.getSignature().getName() : auditable.entityName())
                    .performedBy(performedBy)
                    .user(currentUser)
                    .details("Executed method: " + joinPoint.getSignature().toShortString())
                    .build();

            auditLogRepository.save(logEntry);
            log.info("Recorded audit log: action='{}', entity='{}', performedBy='{}'", auditable.action(), logEntry.getEntityName(), performedBy);
        } catch (Exception e) {
            log.error("Failed to record audit log: {}", e.getMessage(), e);
        }
    }
}
