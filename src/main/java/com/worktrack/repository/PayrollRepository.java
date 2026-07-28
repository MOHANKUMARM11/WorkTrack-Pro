package com.worktrack.repository;

import com.worktrack.constants.PayrollStatus;
import com.worktrack.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployeeIdAndMonthAndYear(
            Long employeeId,
            Integer month,
            Integer year
    );

    long count();

    long countByStatus(PayrollStatus status);
}