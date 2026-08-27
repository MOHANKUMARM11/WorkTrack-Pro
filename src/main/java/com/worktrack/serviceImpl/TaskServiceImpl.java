package com.worktrack.serviceImpl;

import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.request.TaskRequest;
import com.worktrack.dto.response.TaskResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Task;
import com.worktrack.entity.TaskAssignment;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.exception.custom.TaskNotFoundException;
import com.worktrack.exception.custom.TaskTitleAlreadyExistsException;
import com.worktrack.mapper.TaskMapper;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.notification.event.TaskAssignedEvent;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.TaskAssignmentRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final NotificationEventProducer notificationEventProducer;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        if (taskRepository.existsByTitle(request.getTitle())) {
            throw new TaskTitleAlreadyExistsException("Task title already exists");
        }

        Employee employee = null;
        if (request.getEmployeeId() != null) {
            employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        Task task = TaskMapper.toEntity(request, employee, company);
        Task savedTask = taskRepository.save(task);

        if (employee != null) {
            TaskAssignment initialAssignment = TaskAssignment.builder()
                    .task(savedTask)
                    .employee(employee)
                    .build();
            taskAssignmentRepository.save(initialAssignment);
            savedTask.getAssignments().add(initialAssignment);

            notificationEventProducer.publishTaskAssigned(
                    new TaskAssignedEvent(savedTask.getId(), employee.getId(), savedTask.getTitle()));
        }

        return TaskMapper.toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return TaskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException("Employee not found");
        }
        List<Long> taskIds = taskAssignmentRepository.findByEmployeeId(employeeId).stream()
                .map(assignment -> assignment.getTask().getId())
                .distinct()
                .toList();

        return taskRepository.findAllById(taskIds).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByCompany(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return taskRepository.findByCompanyId(companyId).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Override
    public TaskResponse assignEmployeesToTask(Long taskId, List<Long> employeeIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        for (Long empId : employeeIds) {
            if (!taskAssignmentRepository.existsByTaskIdAndEmployeeId(taskId, empId)) {
                Employee employee = employeeRepository.findById(empId)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + empId));

                TaskAssignment assignment = TaskAssignment.builder()
                        .task(task)
                        .employee(employee)
                        .build();
                taskAssignmentRepository.save(assignment);
                task.getAssignments().add(assignment);

                notificationEventProducer.publishTaskAssigned(
                        new TaskAssignedEvent(task.getId(), employee.getId(), task.getTitle()));
            }
        }

        return TaskMapper.toResponse(task);
    }

    @Override
    public TaskResponse unassignEmployeeFromTask(Long taskId, Long employeeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        TaskAssignment assignment = taskAssignmentRepository.findByTaskIdAndEmployeeId(taskId, employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found for employee id: " + employeeId));

        taskAssignmentRepository.delete(assignment);
        task.getAssignments().remove(assignment);

        return TaskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        taskRepository.findByTitle(request.getTitle())
                .ifPresent(existingTask -> {
                    if (!existingTask.getId().equals(task.getId())) {
                        throw new TaskTitleAlreadyExistsException("Task title already exists");
                    }
                });

        Employee employee = null;
        if (request.getEmployeeId() != null) {
            employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEmployee(employee);
        task.setCompany(company);

        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        task.setStatus(status);
        return TaskMapper.toResponse(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        taskRepository.delete(task);
    }
}