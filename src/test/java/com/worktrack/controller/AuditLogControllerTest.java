package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.response.AuditLogResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditLogController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/audit-logs should return all audit logs")
    void getAllAuditLogs_ReturnsList() throws Exception {
        AuditLogResponse response = AuditLogResponse.builder()
                .id(100L)
                .action("USER_LOGIN")
                .entityName("User")
                .performedBy("admin@acme.com")
                .build();

        when(auditLogService.getAllAuditLogs()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].action").value("USER_LOGIN"));
    }

    @Test
    @DisplayName("GET /api/v1/audit-logs/company/{companyId} should return company audit logs")
    void getAuditLogsByCompanyId_ReturnsList() throws Exception {
        AuditLogResponse response = AuditLogResponse.builder()
                .id(100L)
                .action("USER_LOGIN")
                .companyId(1L)
                .build();

        when(auditLogService.getAuditLogsByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/audit-logs/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].companyId").value(1));
    }
}
