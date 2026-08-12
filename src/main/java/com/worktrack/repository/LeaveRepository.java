package com.worktrack.repository;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<Leave, Long> {

    boolean existsByEmployeeIdAndStartDateAndEndDate(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByStatus(LeaveStatus status);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(
            Long companyId,
            LeaveStatus status);

    @Query("""
        SELECT COALESCE(SUM(l.totalDays), 0)
        FROM Leave l
        WHERE l.company.id = :companyId
        """)
    Integer sumTotalLeaveDaysByCompanyId(
            @Param("companyId") Long companyId);
}