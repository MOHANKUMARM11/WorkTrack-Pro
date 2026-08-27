package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktrack.dto.request.DeviceRegistrationRequest;
import com.worktrack.entity.Employee;
import com.worktrack.entity.EmployeeDevice;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.EmployeeDeviceService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeDeviceControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployeeDeviceService employeeDeviceService;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeDeviceController employeeDeviceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeDeviceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/devices/register/{employeeId} should register device and return 201 CREATED")
    void registerDevice_ReturnsCreated() throws Exception {
        DeviceRegistrationRequest request = new DeviceRegistrationRequest("device-uuid-1", "secret123");

        EmployeeDevice device = EmployeeDevice.builder()
                .id(1L)
                .deviceId("device-uuid-1")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(employeeDeviceService.registerDevice(eq(100L), any(DeviceRegistrationRequest.class)))
                .thenReturn(device);

        mockMvc.perform(post("/api/v1/devices/register/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deviceId").value("device-uuid-1"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/devices/employee/{employeeId} should return list of registered devices")
    void getEmployeeDevices_ReturnsList() throws Exception {
        EmployeeDevice device = EmployeeDevice.builder()
                .id(1L)
                .deviceId("device-uuid-1")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(employeeDeviceService.getEmployeeDevices(100L)).thenReturn(List.of(device));

        mockMvc.perform(get("/api/v1/devices/employee/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].deviceId").value("device-uuid-1"));
    }

    @Test
    @DisplayName("PATCH /api/v1/devices/{deviceId}/revoke should return 204 NO CONTENT")
    void revokeDevice_ReturnsNoContent() throws Exception {
        doNothing().when(employeeDeviceService).revokeDevice(100L, 1L);

        mockMvc.perform(patch("/api/v1/devices/1/revoke")
                        .param("employeeId", "100"))
                .andExpect(status().isNoContent());
    }
}
