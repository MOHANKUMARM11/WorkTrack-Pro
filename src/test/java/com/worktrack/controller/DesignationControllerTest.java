package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktrack.dto.request.DesignationRequest;
import com.worktrack.dto.response.DesignationResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.DesignationService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DesignationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DesignationService designationService;

    @InjectMocks
    private DesignationController designationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(designationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/designations should create designation and return 201 CREATED")
    void createDesignation_ReturnsCreated() throws Exception {
        DesignationRequest request = DesignationRequest.builder()
                .title("Senior Software Engineer")
                .description("Lead developer")
                .companyId(1L)
                .build();

        DesignationResponse response = DesignationResponse.builder()
                .id(20L)
                .title("Senior Software Engineer")
                .companyId(1L)
                .build();

        when(designationService.createDesignation(any(DesignationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/designations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.title").value("Senior Software Engineer"));
    }

    @Test
    @DisplayName("GET /api/v1/designations/{id} should return 200 OK")
    void getDesignationById_ReturnsOk() throws Exception {
        DesignationResponse response = DesignationResponse.builder()
                .id(20L)
                .title("Senior Software Engineer")
                .companyId(1L)
                .build();

        when(designationService.getDesignationById(20L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/designations/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.title").value("Senior Software Engineer"));
    }

    @Test
    @DisplayName("GET /api/v1/designations/company/{companyId} should return list of designations")
    void getDesignationsByCompanyId_ReturnsList() throws Exception {
        DesignationResponse response = DesignationResponse.builder()
                .id(20L)
                .title("Senior Software Engineer")
                .companyId(1L)
                .build();

        when(designationService.getDesignationsByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/designations/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].title").value("Senior Software Engineer"));
    }

    @Test
    @DisplayName("DELETE /api/v1/designations/{id} should return 204 NO CONTENT")
    void deleteDesignation_ReturnsNoContent() throws Exception {
        doNothing().when(designationService).deleteDesignation(20L);

        mockMvc.perform(delete("/api/v1/designations/20"))
                .andExpect(status().isNoContent());
    }
}
