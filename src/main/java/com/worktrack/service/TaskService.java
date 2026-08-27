package com.worktrack.service;

import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.request.TaskRequest;
import com.worktrack.dto.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);

    TaskResponse getTaskById(Long id);

    List<TaskResponse> getAllTasks();

    List<TaskResponse> getTasksByEmployee(Long employeeId);

    List<TaskResponse> getTasksByCompany(Long companyId);

    TaskResponse assignEmployeesToTask(Long taskId, List<Long> employeeIds);

    TaskResponse unassignEmployeeFromTask(Long taskId, Long employeeId);

    TaskResponse updateTask(Long id, TaskRequest request);

    TaskResponse updateTaskStatus(Long id, TaskStatus status);

    void deleteTask(Long id);
}