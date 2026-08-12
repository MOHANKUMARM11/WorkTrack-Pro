package com.worktrack.serviceImpl;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.response.DepartmentAnalyticsResponse;
import com.worktrack.entity.Department;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.DepartmentRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.service.DepartmentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentAnalyticsServiceImpl
        implements DepartmentAnalyticsService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final LeaveRepository leaveRepository;

    @Override
    public List<DepartmentAnalyticsResponse> getDepartmentAnalytics(
            Long companyId) {

        companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        List<Department> departments =
                departmentRepository.findByCompanyId(companyId);

        return departments.stream()
                .map(department -> {

                    Long departmentId = department.getId();

                    long employeeCount =
                            employeeRepository
                                    .countByDepartmentId(departmentId);

                    long totalTasks =
                            taskRepository
                                    .countByEmployeeDepartmentId(
                                            departmentId);

                    long completedTasks =
                            taskRepository
                                    .countByEmployeeDepartmentIdAndStatus(
                                            departmentId,
                                            TaskStatus.COMPLETED);

                    long pendingLeaves =
                            leaveRepository
                                    .countByEmployeeDepartmentIdAndStatus(
                                            departmentId,
                                            LeaveStatus.PENDING);

                    double completionRate = totalTasks > 0
                            ? ((double) completedTasks / totalTasks) * 100
                            : 0.0;

                    return DepartmentAnalyticsResponse.builder()
                            .departmentId(departmentId)
                            .departmentName(department.getName())
                            .employeeCount(employeeCount)
                            .totalTasks(totalTasks)
                            .completedTasks(completedTasks)
                            .pendingLeaves(pendingLeaves)
                            .taskCompletionRate(completionRate)
                            .build();
                })
                .toList();
    }
}