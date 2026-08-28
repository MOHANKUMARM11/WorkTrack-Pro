package com.worktrack.service;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.response.DashboardAnalyticsResponse;
import com.worktrack.entity.Company;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.*;
import com.worktrack.serviceImpl.DashboardAnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardAnalyticsServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private DashboardAnalyticsServiceImpl dashboardService;

    private Company sampleCompany;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);
    }

    @Test
    @DisplayName("Should return dashboard analytics summary")
    void getDashboardAnalytics_Success() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(employeeRepository.countByCompanyId(1L)).thenReturn(25L);
        when(taskRepository.countByCompanyId(1L)).thenReturn(40L);
        when(taskRepository.countByCompanyIdAndStatus(1L, TaskStatus.COMPLETED)).thenReturn(30L);
        when(leaveRepository.countByCompanyIdAndStatus(1L, LeaveStatus.PENDING)).thenReturn(3L);
        when(attendanceRepository.countByCompanyId(1L)).thenReturn(500L);
        when(attendanceRepository.findAverageWorkingHoursByCompanyId(1L)).thenReturn(8.2);

        DashboardAnalyticsResponse response = dashboardService.getDashboardAnalytics(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTotalEmployees()).isEqualTo(25L);
        assertThat(response.getCompletedTasks()).isEqualTo(30L);
        assertThat(response.getTaskCompletionRate()).isEqualTo(75.0);
        assertThat(response.getAverageWorkingHours()).isEqualTo(8.2);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company ID invalid")
    void getDashboardAnalytics_InvalidCompany_ThrowsException() {
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dashboardService.getDashboardAnalytics(99L))
                .isInstanceOf(CompanyNotFoundException.class);
    }
}
