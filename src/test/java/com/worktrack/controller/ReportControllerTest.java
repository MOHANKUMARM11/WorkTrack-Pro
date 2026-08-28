package com.worktrack.controller;

import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.ReportExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportExportService reportExportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/reports/attendance/csv should return CSV file content")
    void exportAttendanceReportCsv_ReturnsFile() throws Exception {
        byte[] csvBytes = "Attendance ID,Employee ID,Date,Check-In,Check-Out,Working Hours,Status\n100,10,2026-09-01,09:00,17:00,8.0,PRESENT\n".getBytes(StandardCharsets.UTF_8);

        when(reportExportService.exportAttendanceReportCsv(eq(1L), any(), any())).thenReturn(csvBytes);

        mockMvc.perform(get("/api/v1/reports/attendance/csv").param("companyId", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=attendance_report.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(new String(csvBytes, StandardCharsets.UTF_8)));
    }
}
