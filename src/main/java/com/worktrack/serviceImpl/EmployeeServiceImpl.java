package com.worktrack.serviceImpl;

import com.worktrack.constants.EmployeeStatus;
import com.worktrack.dto.request.EmployeeRequest;
import com.worktrack.dto.response.EmployeeResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.mapper.EmployeeMapper;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (employeeRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Employee employee = EmployeeMapper.toEntity(request, company);

        employee.setPassword(passwordEncoder.encode(request.getPassword()));

        Employee savedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return EmployeeMapper.toResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employeeRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(employee.getId())) {
                        throw new RuntimeException("Email already exists");
                    }
                });

        employeeRepository.findByPhone(request.getPhone())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(employee.getId())) {
                        throw new RuntimeException("Phone number already exists");
                    }
                });

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setRole(request.getRole());
        employee.setCompany(company);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public EmployeeResponse updateEmployeeStatus(Long id, EmployeeStatus status) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setStatus(status);

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setStatus(EmployeeStatus.INACTIVE);

        employeeRepository.save(employee);
    }
}