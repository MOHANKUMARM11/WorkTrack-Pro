package com.worktrack.mapper;

import com.worktrack.constants.EmployeeStatus;
import com.worktrack.dto.request.EmployeeRequest;
import com.worktrack.dto.response.EmployeeResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;

public class EmployeeMapper {

    private EmployeeMapper() {}

    public static Employee toEntity(EmployeeRequest request, Company company) {

        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword()) // We'll encode this in the service
                .role(request.getRole())
                .status(EmployeeStatus.ACTIVE)
                .company(company)
                .build();
    }

    public static EmployeeResponse toResponse(Employee employee) {

        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .role(employee.getRole())
                .status(employee.getStatus())
                .companyId(employee.getCompany().getId())
                .companyName(employee.getCompany().getName())
                .build();
    }
}