package com.worktrack.repository;

import com.worktrack.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Employee> findByPhone(String phone);

    boolean existsByPhone(String phone);

    long count();

    long countByCompanyId(Long companyId);

    long countByDepartmentId(Long departmentId);
}