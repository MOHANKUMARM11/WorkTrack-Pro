package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.SystemSettingRequest;
import com.worktrack.dto.response.SystemSettingResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.SystemSettingService;
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
class SystemSettingControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private SystemSettingService systemSettingService;

    @InjectMocks
    private SystemSettingController systemSettingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(systemSettingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/system-settings should save setting and return 201 CREATED")
    void saveOrUpdateSetting_ReturnsCreated() throws Exception {
        SystemSettingRequest request = SystemSettingRequest.builder()
                .key("GEOFENCE_RADIUS_METERS")
                .value("200")
                .category("ATTENDANCE")
                .companyId(1L)
                .build();

        SystemSettingResponse response = SystemSettingResponse.builder()
                .id(50L)
                .key("GEOFENCE_RADIUS_METERS")
                .value("200")
                .companyId(1L)
                .build();

        when(systemSettingService.saveOrUpdateSetting(any(SystemSettingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/system-settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.key").value("GEOFENCE_RADIUS_METERS"));
    }

    @Test
    @DisplayName("GET /api/v1/system-settings/company/{companyId} should return company settings list")
    void getSettingsByCompany_ReturnsList() throws Exception {
        SystemSettingResponse response = SystemSettingResponse.builder()
                .id(50L)
                .key("GEOFENCE_RADIUS_METERS")
                .value("200")
                .companyId(1L)
                .build();

        when(systemSettingService.getSettingsByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/system-settings/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50))
                .andExpect(jsonPath("$[0].key").value("GEOFENCE_RADIUS_METERS"));
    }
}
