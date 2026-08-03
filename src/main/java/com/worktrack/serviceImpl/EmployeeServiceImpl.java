package com.worktrack.serviceImpl;

import com.worktrack.constants.EmployeeStatus;
import com.worktrack.dto.request.EmployeeRequest;
import com.worktrack.dto.response.EmployeeResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.exception.custom.EmailAlreadyExistsException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.exception.custom.PhoneAlreadyExistsException;
import com.worktrack.mapper.EmployeeMapper;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.worktrack.entity.User;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        // Check duplicate employee email
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Check duplicate employee phone
        if (employeeRepository.existsByPhone(request.getPhone())) {
            throw new PhoneAlreadyExistsException("Phone number already exists");
        }

        // Check duplicate user email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        // Create Employee
        Employee employee = EmployeeMapper.toEntity(request, company);
        employee.setPassword(passwordEncoder.encode(request.getPassword()));

        Employee savedEmployee = employeeRepository.save(employee);

        // Create User
        User user = User.builder()
                .firstName(savedEmployee.getFirstName())
                .lastName(savedEmployee.getLastName())
                .email(savedEmployee.getEmail())
                .password(savedEmployee.getPassword()) // already encoded
                .phone(savedEmployee.getPhone())
                .role(savedEmployee.getRole())
                .company(company)
                .enabled(true)
                .build();

        userRepository.save(user);

        return EmployeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

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
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        // Save old email before updating
        String oldEmail = employee.getEmail();

        // Check duplicate employee email
        employeeRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(employee.getId())) {
                        throw new EmailAlreadyExistsException("Email already exists");
                    }
                });

        // Check duplicate user email
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getEmail().equals(oldEmail)) {
                        throw new EmailAlreadyExistsException("Email already exists");
                    }
                });

        // Check duplicate phone
        employeeRepository.findByPhone(request.getPhone())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(employee.getId())) {
                        throw new PhoneAlreadyExistsException("Phone number already exists");
                    }
                });

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        // Update Employee
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

        // Find corresponding User using old email
        User user = userRepository.findByEmail(oldEmail)
                .orElseThrow(() ->
                        new RuntimeException("Associated user not found"));

        // Update User
        user.setFirstName(updatedEmployee.getFirstName());
        user.setLastName(updatedEmployee.getLastName());
        user.setEmail(updatedEmployee.getEmail());
        user.setPhone(updatedEmployee.getPhone());
        user.setRole(updatedEmployee.getRole());
        user.setCompany(company);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(updatedEmployee.getPassword()); // already encoded
        }

        userRepository.save(user);

        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public EmployeeResponse updateEmployeeStatus(Long id, EmployeeStatus status) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        employee.setStatus(status);

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found"));

        // Deactivate employee
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);

        // Disable corresponding user
        User user = userRepository.findByEmail(employee.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Associated user not found"));

        user.setEnabled(false);

        userRepository.save(user);
    }
}