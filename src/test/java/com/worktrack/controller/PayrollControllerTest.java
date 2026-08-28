package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktrack.constants.PayrollStatus;
import com.worktrack.dto.request.PayrollRequest;
import com.worktrack.dto.response.PayrollResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.PayrollService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PayrollControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PayrollService payrollService;

    @InjectMocks
    private PayrollController payrollController;

    private PayrollResponse sampleResponse;
    private PayrollRequest sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(payrollController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleResponse = PayrollResponse.builder()
                .id(100L)
                .employeeId(10L)
                .companyId(1L)
                .month(9)
                .year(2026)
                .basicSalary(BigDecimal.valueOf(5000.00))
                .netSalary(BigDecimal.valueOf(5300.00))
                .status(PayrollStatus.PENDING)
                .build();

        sampleRequest = new PayrollRequest();
        sampleRequest.setEmployeeId(10L);
        sampleRequest.setCompanyId(1L);
        sampleRequest.setMonth(9);
        sampleRequest.setYear(2026);
        sampleRequest.setBasicSalary(BigDecimal.valueOf(5000.00));
        sampleRequest.setAllowance(BigDecimal.valueOf(500.00));
        sampleRequest.setBonus(BigDecimal.valueOf(200.00));
        sampleRequest.setDeduction(BigDecimal.valueOf(100.00));
        sampleRequest.setTax(BigDecimal.valueOf(300.00));
    }

    @Test
    @DisplayName("POST /api/v1/payroll should create payroll")
    void createPayroll_Returns201() throws Exception {
        when(payrollService.createPayroll(any(PayrollRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/payroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @DisplayName("GET /api/v1/payroll/{id} should return payroll")
    void getPayrollById_ReturnsPayroll() throws Exception {
        when(payrollService.getPayrollById(100L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/payroll/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @DisplayName("GET /api/v1/payroll should return all payrolls")
    void getAllPayrolls_ReturnsList() throws Exception {
        when(payrollService.getAllPayrolls()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/v1/payroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    @DisplayName("PATCH /api/v1/payroll/{id}/status should update status")
    void updatePayrollStatus_ReturnsUpdated() throws Exception {
        when(payrollService.updatePayrollStatus(eq(100L), any())).thenReturn(sampleResponse);

        mockMvc.perform(patch("/api/v1/payroll/100/status").param("status", "PAID"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/payroll/{id} should delete payroll")
    void deletePayroll_Returns204() throws Exception {
        doNothing().when(payrollService).deletePayroll(100L);

        mockMvc.perform(delete("/api/v1/payroll/100"))
                .andExpect(status().isNoContent());
    }
}
