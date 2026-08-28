package com.worktrack.controller;

import com.worktrack.dto.response.DashboardAnalyticsResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.DashboardAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardAnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardAnalyticsService dashboardAnalyticsService;

    @InjectMocks
    private DashboardAnalyticsController dashboardAnalyticsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardAnalyticsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/dashboard should return dashboard metrics")
    void getDashboardAnalytics_ReturnsMetrics() throws Exception {
        DashboardAnalyticsResponse response = DashboardAnalyticsResponse.builder()
                .totalEmployees(25L)
                .totalTasks(40L)
                .completedTasks(30L)
                .pendingLeaves(3L)
                .taskCompletionRate(75.0)
                .averageWorkingHours(8.2)
                .build();

        when(dashboardAnalyticsService.getDashboardAnalytics(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard").param("companyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEmployees").value(25))
                .andExpect(jsonPath("$.data.taskCompletionRate").value(75.0));
    }
}
