package com.worktrack.controller;

import com.worktrack.dto.response.TaskPerformanceAnalyticsResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.TaskPerformanceAnalyticsService;
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
class TaskPerformanceAnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskPerformanceAnalyticsService taskPerformanceAnalyticsService;

    @InjectMocks
    private TaskPerformanceAnalyticsController analyticsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/analytics/tasks should return task performance metrics")
    void getTaskPerformanceAnalytics_ReturnsMetrics() throws Exception {
        TaskPerformanceAnalyticsResponse response = TaskPerformanceAnalyticsResponse.builder()
                .totalTasks(10L)
                .completedCount(5L)
                .completionRate(50.0)
                .build();

        when(taskPerformanceAnalyticsService.getTaskPerformanceAnalytics(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/tasks").param("companyId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalTasks").value(10))
                .andExpect(jsonPath("$.data.completionRate").value(50.0));
    }
}
