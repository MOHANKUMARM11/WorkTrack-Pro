package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceCheckInRequest;
import com.worktrack.dto.request.AttendanceCheckOutRequest;
import com.worktrack.dto.request.BreakRequest;
import com.worktrack.dto.request.ManualCheckInApprovalRequest;
import com.worktrack.dto.response.AttendanceCheckInResponse;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.dto.response.AttendanceTodayResponse;
import com.worktrack.dto.response.BreakResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.AttendanceService;
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
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AttendanceControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AttendanceController attendanceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/attendance/check-in/{employeeId} should return 201 CREATED")
    void checkIn_ReturnsCreated() throws Exception {
        AttendanceCheckInRequest request = new AttendanceCheckInRequest(
                12.971599, 77.594566, 10.0, "device-1", "secret123", null, null, null, null
        );

        AttendanceCheckInResponse response = new AttendanceCheckInResponse(
                500L, 100L, 1L, LocalDate.now(), LocalTime.of(9, 0), null, null, AttendanceStatus.PRESENT, "GPS", 15.0, true, LocalDateTime.now()
        );

        when(attendanceService.checkIn(eq(100L), any(AttendanceCheckInRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/attendance/check-in/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attendanceId").value(500))
                .andExpect(jsonPath("$.withinGeofence").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/attendance/check-out/{employeeId} should return 200 OK")
    void checkOut_ReturnsOk() throws Exception {
        AttendanceCheckOutRequest request = new AttendanceCheckOutRequest(
                12.971599, 77.594566, 10.0, "device-1", "secret123", null, null
        );

        AttendanceCheckInResponse response = new AttendanceCheckInResponse(
                500L, 100L, 1L, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, "GPS", 15.0, true, LocalDateTime.now()
        );

        when(attendanceService.checkOut(eq(100L), any(AttendanceCheckOutRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/attendance/check-out/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendanceId").value(500));
    }

    @Test
    @DisplayName("GET /api/v1/attendance/today/{employeeId} should return 200 OK with today status")
    void getTodayAttendance_ReturnsTodayResponse() throws Exception {
        AttendanceTodayResponse response = new AttendanceTodayResponse(
                500L, 100L, LocalDate.now(), LocalTime.of(9, 0), null, null, AttendanceStatus.PRESENT, true, false
        );

        when(attendanceService.getTodayAttendance(100L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/attendance/today/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attendanceId").value(500))
                .andExpect(jsonPath("$.checkedIn").value(true))
                .andExpect(jsonPath("$.checkedOut").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/attendance/break/start should return 201 CREATED")
    void startBreak_ReturnsCreated() throws Exception {
        BreakRequest request = new BreakRequest(500L);
        BreakResponse response = new BreakResponse(1L, 500L, LocalDateTime.now(), null, null);

        when(attendanceService.startBreak(eq(100L), any(BreakRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/attendance/break/start")
                        .param("employeeId", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.attendanceId").value(500));
    }

    @Test
    @DisplayName("POST /api/v1/attendance/{id}/manual-approval should return 200 OK")
    void approveManualCheckIn_ReturnsOk() throws Exception {
        ManualCheckInApprovalRequest request = new ManualCheckInApprovalRequest("Verified on site", null);
        AttendanceResponse response = AttendanceResponse.builder().id(500L).status(AttendanceStatus.PRESENT).build();

        when(attendanceService.approveManualCheckIn(eq(500L), eq(200L), any(ManualCheckInApprovalRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/attendance/500/manual-approval")
                        .param("managerId", "200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(500));
    }
}
