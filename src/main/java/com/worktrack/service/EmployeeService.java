package com.worktrack.service;

import com.worktrack.constants.EmployeeStatus;
import com.worktrack.dto.request.EmployeeRequest;
import com.worktrack.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(Long id);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    EmployeeResponse updateEmployeeStatus(Long id, EmployeeStatus status);

    void deleteEmployee(Long id);
}