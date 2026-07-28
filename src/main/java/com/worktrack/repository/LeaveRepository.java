package com.worktrack.repository;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    boolean existsByEmployeeIdAndStartDateAndEndDate(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByStatus(LeaveStatus status);
}