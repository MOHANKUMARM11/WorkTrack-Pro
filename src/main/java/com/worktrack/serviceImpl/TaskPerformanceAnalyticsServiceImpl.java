package com.worktrack.serviceImpl;

import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.response.TaskPerformanceAnalyticsResponse;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.service.TaskPerformanceAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskPerformanceAnalyticsServiceImpl
        implements TaskPerformanceAnalyticsService {

    private final TaskRepository taskRepository;
    private final CompanyRepository companyRepository;

    @Override
    public TaskPerformanceAnalyticsResponse getTaskPerformanceAnalytics(
            Long companyId) {

        companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        long totalTasks =
                taskRepository.countByCompanyId(companyId);

        long todoCount =
                taskRepository.countByCompanyIdAndStatus(
                        companyId,
                        TaskStatus.TODO);

        long inProgressCount =
                taskRepository.countByCompanyIdAndStatus(
                        companyId,
                        TaskStatus.IN_PROGRESS);

        long completedCount =
                taskRepository.countByCompanyIdAndStatus(
                        companyId,
                        TaskStatus.COMPLETED);

        long overdueCount =
                taskRepository.countOverdueTasksByCompanyId(companyId);

        double completionRate = totalTasks > 0
                ? ((double) completedCount / totalTasks) * 100
                : 0.0;

        return TaskPerformanceAnalyticsResponse.builder()
                .totalTasks(totalTasks)
                .todoCount(todoCount)
                .inProgressCount(inProgressCount)
                .completedCount(completedCount)
                .overdueCount(overdueCount)
                .completionRate(completionRate)
                .build();
    }
}