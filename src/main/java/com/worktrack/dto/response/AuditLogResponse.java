package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;

    private String action;

    private String entityName;

    private String entityId;

    private String performedBy;

    private Long userId;

    private Long companyId;

    private String details;

    private String ipAddress;

    private LocalDateTime createdAt;
}
