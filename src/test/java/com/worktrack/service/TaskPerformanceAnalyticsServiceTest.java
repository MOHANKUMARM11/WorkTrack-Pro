package com.worktrack.service;

import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.response.TaskPerformanceAnalyticsResponse;
import com.worktrack.entity.Company;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.serviceImpl.TaskPerformanceAnalyticsServiceImpl;
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
class TaskPerformanceAnalyticsServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private TaskPerformanceAnalyticsServiceImpl analyticsService;

    private Company sampleCompany;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);
    }

    @Test
    @DisplayName("Should return task performance analytics for company")
    void getTaskPerformanceAnalytics_Success() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(taskRepository.countByCompanyId(1L)).thenReturn(10L);
        when(taskRepository.countByCompanyIdAndStatus(1L, TaskStatus.TODO)).thenReturn(2L);
        when(taskRepository.countByCompanyIdAndStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(3L);
        when(taskRepository.countByCompanyIdAndStatus(1L, TaskStatus.COMPLETED)).thenReturn(5L);
        when(taskRepository.countOverdueTasksByCompanyId(1L)).thenReturn(1L);

        TaskPerformanceAnalyticsResponse response = analyticsService.getTaskPerformanceAnalytics(1L);

        assertThat(response).isNotNull();
        assertThat(response.getTotalTasks()).isEqualTo(10L);
        assertThat(response.getCompletedCount()).isEqualTo(5L);
        assertThat(response.getCompletionRate()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company ID invalid")
    void getTaskPerformanceAnalytics_InvalidCompany_ThrowsException() {
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> analyticsService.getTaskPerformanceAnalytics(99L))
                .isInstanceOf(CompanyNotFoundException.class);
    }
}
