package com.worktrack.service;

import com.worktrack.dto.response.AuditLogResponse;
import com.worktrack.entity.AuditLog;
import com.worktrack.entity.Company;
import com.worktrack.entity.User;
import com.worktrack.repository.AuditLogRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.serviceImpl.AuditLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private Company sampleCompany;
    private User sampleUser;
    private AuditLog sampleAuditLog;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleUser = User.builder().email("admin@acme.com").build();
        ReflectionTestUtils.setField(sampleUser, "id", 10L);

        sampleAuditLog = AuditLog.builder()
                .action("USER_LOGIN")
                .entityName("User")
                .entityId("10")
                .performedBy("admin@acme.com")
                .user(sampleUser)
                .company(sampleCompany)
                .details("User logged in successfully")
                .build();
        ReflectionTestUtils.setField(sampleAuditLog, "id", 100L);
    }

    @Test
    @DisplayName("Should record audit log entry")
    void recordAuditLog_Success() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(sampleAuditLog);

        AuditLogResponse response = auditLogService.recordAuditLog("USER_LOGIN", "User", "10", "User logged in successfully", "127.0.0.1");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getAction()).isEqualTo("USER_LOGIN");
    }

    @Test
    @DisplayName("Should return audit logs by company ID")
    void getAuditLogsByCompanyId_Success() {
        when(auditLogRepository.findByCompanyIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(sampleAuditLog));

        List<AuditLogResponse> responses = auditLogService.getAuditLogsByCompanyId(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAction()).isEqualTo("USER_LOGIN");
    }
}
