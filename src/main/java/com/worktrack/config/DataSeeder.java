package com.worktrack.config;

import com.worktrack.constants.CompanyStatus;
import com.worktrack.constants.EmployeeStatus;
import com.worktrack.constants.SubscriptionPlan;
import com.worktrack.constants.UserRole;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.User;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (companyRepository.count() > 0) {
            return;
        }

        Company company = Company.builder()
                .name("TechNova Global")
                .registrationNumber("REG-1001")
                .industry("Software")
                .subscriptionPlan(SubscriptionPlan.ENTERPRISE)
                .status(CompanyStatus.ACTIVE)
                .timezone("Asia/Colombo")
                .build();

        company = companyRepository.save(company);

        createEmployee(
                "System",
                "Admin",
                "admin@example.com",
                "9000000001",
                UserRole.ADMIN,
                company
        );

        createEmployee(
                "System",
                "Manager",
                "manager@example.com",
                "9000000002",
                UserRole.MANAGER,
                company
        );

        createEmployee(
                "Bob",
                "Employee",
                "bob.employee@example.com",
                "9000000003",
                UserRole.EMPLOYEE,
                company
        );

        System.out.println("========== SAMPLE DATA CREATED ==========");
    }

    private void createEmployee(
            String firstName,
            String lastName,
            String email,
            String phone,
            UserRole role,
            Company company
    ) {

        String encodedPassword = passwordEncoder.encode("password123");

        Employee employee = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(encodedPassword)
                .role(role)
                .status(EmployeeStatus.ACTIVE)
                .company(company)
                .build();

        employeeRepository.save(employee);

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .password(encodedPassword)
                .role(role)
                .company(company)
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}