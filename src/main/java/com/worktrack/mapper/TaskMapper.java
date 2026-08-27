package com.worktrack.mapper;

import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.request.TaskRequest;
import com.worktrack.dto.response.TaskAssignmentResponse;
import com.worktrack.dto.response.TaskResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Task;

import java.util.Collections;

public class TaskMapper {

    private TaskMapper() {
    }

    public static Task toEntity(
            TaskRequest request,
            Employee employee,
            Company company) {

        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TaskStatus.TODO)
                .dueDate(request.getDueDate())
                .employee(employee)
                .company(company)
                .build();
    }

    public static TaskResponse toResponse(Task task) {
        var assignmentResponses = (task.getAssignments() != null) ?
                task.getAssignments().stream()
                        .map(a -> TaskAssignmentResponse.builder()
                                .id(a.getId())
                                .taskId(task.getId())
                                .employeeId(a.getEmployee().getId())
                                .employeeName(a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName())
                                .assignedAt(a.getAssignedAt())
                                .build())
                        .toList() : Collections.<TaskAssignmentResponse>emptyList();

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .employeeId(task.getEmployee() != null ? task.getEmployee().getId() : null)
                .employeeName(task.getEmployee() != null ?
                        (task.getEmployee().getFirstName() + " " + task.getEmployee().getLastName()) : null)
                .companyId(task.getCompany().getId())
                .companyName(task.getCompany().getName())
                .assignments(assignmentResponses)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}