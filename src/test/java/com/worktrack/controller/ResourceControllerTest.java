package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktrack.constants.ResourceStatus;
import com.worktrack.dto.request.ResourceRequest;
import com.worktrack.dto.response.ResourceResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.ResourceService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ResourceControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    private ResourceResponse sampleResponse;
    private ResourceRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = ResourceResponse.builder()
                .id(50L)
                .name("MacBook Pro M3")
                .type("HARDWARE")
                .status(ResourceStatus.ASSIGNED)
                .companyId(1L)
                .employeeId(10L)
                .build();

        sampleRequest = new ResourceRequest();
        sampleRequest.setName("MacBook Pro M3");
        sampleRequest.setDescription("Developer laptop");
        sampleRequest.setType("HARDWARE");
        sampleRequest.setQuantity(1);
        sampleRequest.setStatus(ResourceStatus.ASSIGNED);
        sampleRequest.setCompanyId(1L);
        sampleRequest.setEmployeeId(10L);
    }

    @Test
    @DisplayName("POST /api/v1/resources should create resource")
    void createResource_Returns201() throws Exception {
        when(resourceService.createResource(any(ResourceRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(50))
                .andExpect(jsonPath("$.data.name").value("MacBook Pro M3"));
    }

    @Test
    @DisplayName("GET /api/v1/resources/{id} should return resource")
    void getResourceById_ReturnsResource() throws Exception {
        when(resourceService.getResourceById(50L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/resources/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(50));
    }

    @Test
    @DisplayName("GET /api/v1/resources should return all resources")
    void getAllResources_ReturnsList() throws Exception {
        when(resourceService.getAllResources()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(50));
    }

    @Test
    @DisplayName("PATCH /api/v1/resources/{id}/status should update status")
    void updateResourceStatus_ReturnsUpdated() throws Exception {
        when(resourceService.updateResourceStatus(eq(50L), any())).thenReturn(sampleResponse);

        mockMvc.perform(patch("/api/v1/resources/50/status").param("status", "AVAILABLE"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/resources/{id} should delete resource")
    void deleteResource_Returns200() throws Exception {
        doNothing().when(resourceService).deleteResource(50L);

        mockMvc.perform(delete("/api/v1/resources/50"))
                .andExpect(status().isOk());
    }
}
