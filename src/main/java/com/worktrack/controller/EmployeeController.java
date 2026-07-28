package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.request.EmployeeRequest;
import com.worktrack.dto.request.EmployeeStatusRequest;
import com.worktrack.dto.response.EmployeeResponse;
import com.worktrack.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        System.out.println("===== CREATE EMPLOYEE API HIT =====");

        EmployeeResponse response = employeeService.createEmployee(request);

        return ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponse> getEmployeeById(
            @PathVariable Long id) {

        EmployeeResponse response = employeeService.getEmployeeById(id);

        return ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee retrieved successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<EmployeeResponse>> getAllEmployees() {

        List<EmployeeResponse> response = employeeService.getAllEmployees();

        return ApiResponse.<List<EmployeeResponse>>builder()
                .success(true)
                .message("Employees retrieved successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        EmployeeResponse response = employeeService.updateEmployee(id, request);

        return ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee updated successfully")
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<EmployeeResponse> updateEmployeeStatus(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeStatusRequest request) {

        EmployeeResponse response =
                employeeService.updateEmployeeStatus(id, request.getStatus());

        return ApiResponse.<EmployeeResponse>builder()
                .success(true)
                .message("Employee status updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Employee deleted successfully")
                .build();
    }
}