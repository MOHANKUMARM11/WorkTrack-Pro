package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.HolidayRequest;
import com.worktrack.dto.response.HolidayResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.HolidayService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HolidayControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private HolidayService holidayService;

    @InjectMocks
    private HolidayController holidayController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(holidayController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/holidays should create holiday and return 201 CREATED")
    void createHoliday_ReturnsCreated() throws Exception {
        HolidayRequest request = HolidayRequest.builder()
                .name("New Year's Day")
                .holidayDate(LocalDate.of(2026, 1, 1))
                .description("Public holiday")
                .companyId(1L)
                .build();

        HolidayResponse response = HolidayResponse.builder()
                .id(100L)
                .name("New Year's Day")
                .holidayDate(LocalDate.of(2026, 1, 1))
                .companyId(1L)
                .build();

        when(holidayService.createHoliday(any(HolidayRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/holidays")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("New Year's Day"));
    }

    @Test
    @DisplayName("GET /api/v1/holidays/company/{companyId} should return list of holidays")
    void getHolidaysByCompanyId_ReturnsList() throws Exception {
        HolidayResponse response = HolidayResponse.builder()
                .id(100L)
                .name("New Year's Day")
                .holidayDate(LocalDate.of(2026, 1, 1))
                .companyId(1L)
                .build();

        when(holidayService.getHolidaysByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/holidays/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].name").value("New Year's Day"));
    }
}
