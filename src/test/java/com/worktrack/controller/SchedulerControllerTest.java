package com.worktrack.controller;

import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.scheduler.AttendanceAutoCheckoutScheduler;
import com.worktrack.scheduler.LeaveAccrualScheduler;
import com.worktrack.scheduler.SystemMaintenanceScheduler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SchedulerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AttendanceAutoCheckoutScheduler attendanceAutoCheckoutScheduler;

    @Mock
    private LeaveAccrualScheduler leaveAccrualScheduler;

    @Mock
    private SystemMaintenanceScheduler systemMaintenanceScheduler;

    @InjectMocks
    private SchedulerController schedulerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(schedulerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/admin/jobs/trigger-auto-checkout should execute auto checkout and return 200 OK")
    void triggerAutoCheckout_ReturnsOk() throws Exception {
        when(attendanceAutoCheckoutScheduler.processAutoCheckouts()).thenReturn(3);

        mockMvc.perform(post("/api/v1/admin/jobs/trigger-auto-checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job").value("AttendanceAutoCheckout"))
                .andExpect(jsonPath("$.processedCount").value(3))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("POST /api/v1/admin/jobs/trigger-leave-accrual should execute leave accrual and return 200 OK")
    void triggerLeaveAccrual_ReturnsOk() throws Exception {
        when(leaveAccrualScheduler.processMonthlyLeaveAccrual()).thenReturn(10);

        mockMvc.perform(post("/api/v1/admin/jobs/trigger-leave-accrual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job").value("LeaveAccrual"))
                .andExpect(jsonPath("$.processedCount").value(10))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
