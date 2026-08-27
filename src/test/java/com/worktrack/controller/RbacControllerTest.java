package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.RoleRequest;
import com.worktrack.dto.response.PermissionResponse;
import com.worktrack.dto.response.RoleResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.RbacService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RbacControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private RbacService rbacService;

    @InjectMocks
    private RbacController rbacController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rbacController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/rbac/roles should create role and return 201 CREATED")
    void createRole_ReturnsCreated() throws Exception {
        RoleRequest request = RoleRequest.builder()
                .name("HR_MANAGER")
                .description("HR Manager Role")
                .companyId(1L)
                .build();

        RoleResponse response = RoleResponse.builder()
                .id(100L)
                .name("HR_MANAGER")
                .companyId(1L)
                .build();

        when(rbacService.createRole(any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/rbac/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("HR_MANAGER"));
    }

    @Test
    @DisplayName("GET /api/v1/rbac/permissions should return permissions list")
    void getAllPermissions_ReturnsList() throws Exception {
        PermissionResponse response = PermissionResponse.builder()
                .id(10L)
                .name("ATTENDANCE_READ")
                .build();

        when(rbacService.getAllPermissions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/rbac/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("ATTENDANCE_READ"));
    }
}
