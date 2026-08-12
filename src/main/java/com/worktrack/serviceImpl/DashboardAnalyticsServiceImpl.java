package com.worktrack.serviceImpl;

import com.worktrack.dto.response.DashboardAnalyticsResponse;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.service.DashboardAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardAnalyticsServiceImpl
        implements DashboardAnalyticsService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final LeaveRepository leaveRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public DashboardAnalyticsResponse getDashboardAnalytics(
            Long companyId) {

        companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        long totalEmployees =
                employeeRepository.countByCompanyId(companyId);

        long totalTasks =
                taskRepository.countByCompanyId(companyId);

        long completedTasks =
                taskRepository.countByCompanyIdAndStatus(
                        companyId,
                        com.worktrack.constants.TaskStatus.COMPLETED);

        long pendingLeaves =
                leaveRepository.countByCompanyIdAndStatus(
                        companyId,
                        com.worktrack.constants.LeaveStatus.PENDING);

        long totalAttendanceRecords =
                attendanceRepository.countByCompanyId(companyId);

        Double averageWorkingHours =
                attendanceRepository
                        .findAverageWorkingHoursByCompanyId(companyId);

        double taskCompletionRate = totalTasks > 0
                ? ((double) completedTasks / totalTasks) * 100
                : 0.0;

        return DashboardAnalyticsResponse.builder()
                .totalEmployees(totalEmployees)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingLeaves(pendingLeaves)
                .totalAttendanceRecords(totalAttendanceRecords)
                .taskCompletionRate(taskCompletionRate)
                .averageWorkingHours(
                        averageWorkingHours != null
                                ? averageWorkingHours
                                : 0.0)
                .build();
    }
}