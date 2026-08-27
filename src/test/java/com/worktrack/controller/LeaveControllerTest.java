package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.constants.LeaveStatus;
import com.worktrack.constants.LeaveType;
import com.worktrack.dto.request.LeaveRequest;
import com.worktrack.dto.response.LeaveResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.LeaveService;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LeaveControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private LeaveService leaveService;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private LeaveController leaveController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(leaveController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/leaves should create leave request and return 201 CREATED")
    void createLeave_ReturnsCreated() throws Exception {
        LeaveRequest request = new LeaveRequest();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setReason("Vacation");
        request.setLeaveType(LeaveType.ANNUAL);
        request.setEmployeeId(100L);
        request.setCompanyId(1L);

        LeaveResponse response = LeaveResponse.builder()
                .id(50L)
                .status(LeaveStatus.PENDING)
                .leaveType(LeaveType.ANNUAL)
                .totalDays(3)
                .build();

        when(leaveService.createLeave(any(LeaveRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("PATCH /api/v1/leaves/{id}/status should update status and return 200 OK")
    void updateLeaveStatus_ReturnsOk() throws Exception {
        LeaveResponse response = LeaveResponse.builder()
                .id(50L)
                .status(LeaveStatus.APPROVED)
                .leaveType(LeaveType.ANNUAL)
                .totalDays(3)
                .build();

        when(leaveService.updateLeaveStatus(eq(50L), eq(LeaveStatus.APPROVED))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/leaves/50/status")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
