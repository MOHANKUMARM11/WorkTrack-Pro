package com.worktrack.controller;

import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.request.TaskAssignRequest;
import com.worktrack.dto.request.TaskRequest;
import com.worktrack.dto.response.TaskResponse;
import com.worktrack.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return new ResponseEntity<>(taskService.createTask(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TaskResponse>> getTasksByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.getTasksByEmployee(employeeId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<TaskResponse>> getTasksByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(taskService.getTasksByCompany(companyId));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TaskResponse> assignEmployeesToTask(
            @PathVariable Long id,
            @RequestBody List<Long> employeeIds) {
        return ResponseEntity.ok(taskService.assignEmployeesToTask(id, employeeIds));
    }

    @DeleteMapping("/{id}/assign/{employeeId}")
    public ResponseEntity<TaskResponse> unassignEmployeeFromTask(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(taskService.unassignEmployeeFromTask(id, employeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}