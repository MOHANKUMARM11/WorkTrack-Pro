package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.ShiftAssignmentRequest;
import com.worktrack.dto.request.ShiftRequest;
import com.worktrack.dto.response.ShiftAssignmentResponse;
import com.worktrack.dto.response.ShiftResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.ShiftService;
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
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShiftControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private ShiftService shiftService;

    @InjectMocks
    private ShiftController shiftController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(shiftController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/shifts should create shift and return 201 CREATED")
    void createShift_ReturnsCreated() throws Exception {
        ShiftRequest request = ShiftRequest.builder()
                .name("Morning Shift")
                .companyId(1L)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .gracePeriodMinutes(15)
                .build();

        ShiftResponse response = ShiftResponse.builder()
                .id(100L)
                .name("Morning Shift")
                .companyId(1L)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .gracePeriodMinutes(15)
                .build();

        when(shiftService.createShift(any(ShiftRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Morning Shift"));
    }

    @Test
    @DisplayName("POST /api/v1/shifts/assignments should assign shift and return 201 CREATED")
    void assignShiftToEmployee_ReturnsCreated() throws Exception {
        ShiftAssignmentRequest request = ShiftAssignmentRequest.builder()
                .shiftId(100L)
                .employeeId(10L)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 30))
                .build();

        ShiftAssignmentResponse response = ShiftAssignmentResponse.builder()
                .id(500L)
                .shiftId(100L)
                .shiftName("Morning Shift")
                .employeeId(10L)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 30))
                .build();

        when(shiftService.assignShiftToEmployee(any(ShiftAssignmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/shifts/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(500))
                .andExpect(jsonPath("$.shiftName").value("Morning Shift"));
    }
}
