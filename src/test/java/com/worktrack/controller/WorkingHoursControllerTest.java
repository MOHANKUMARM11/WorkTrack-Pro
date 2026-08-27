package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.WorkingHoursRequest;
import com.worktrack.dto.response.WorkingDaysCalculationResponse;
import com.worktrack.dto.response.WorkingHoursResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.WorkingHoursService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WorkingHoursControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private WorkingHoursService workingHoursService;

    @InjectMocks
    private WorkingHoursController workingHoursController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(workingHoursController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/working-hours should save working hours schedule and return 201 CREATED")
    void saveOrUpdateWorkingHours_ReturnsCreated() throws Exception {
        WorkingHoursRequest request = WorkingHoursRequest.builder()
                .companyId(1L)
                .dayOfWeek("MONDAY")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isWorkingDay(true)
                .build();

        WorkingHoursResponse response = WorkingHoursResponse.builder()
                .id(100L)
                .companyId(1L)
                .dayOfWeek("MONDAY")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isWorkingDay(true)
                .build();

        when(workingHoursService.saveOrUpdateWorkingHours(any(WorkingHoursRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/working-hours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));
    }

    @Test
    @DisplayName("GET /api/v1/working-hours/company/{companyId}/calculate should return working days calculation")
    void calculateWorkingDays_ReturnsCalculation() throws Exception {
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 7);

        WorkingDaysCalculationResponse calculation = WorkingDaysCalculationResponse.builder()
                .companyId(1L)
                .startDate(start)
                .endDate(end)
                .totalCalendarDays(7)
                .netWorkingDays(5)
                .holidaysCount(0)
                .offDaysCount(2)
                .totalWorkingHours(40.0)
                .build();

        when(workingHoursService.calculateWorkingDays(eq(1L), eq(start), eq(end))).thenReturn(calculation);

        mockMvc.perform(get("/api/v1/working-hours/company/1/calculate")
                        .param("startDate", "2026-09-01")
                        .param("endDate", "2026-09-07"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netWorkingDays").value(5))
                .andExpect(jsonPath("$.totalWorkingHours").value(40.0));
    }
}
