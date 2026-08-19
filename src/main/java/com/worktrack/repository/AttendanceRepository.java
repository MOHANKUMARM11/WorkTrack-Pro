package com.worktrack.repository;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByCompanyId(Long companyId);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByStatus(AttendanceStatus status);

    boolean existsByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(
            Long employeeId,
            LocalDate attendanceDate
    );

    List<Attendance>
    findByEmployeeIdOrderByAttendanceDateDesc(
            Long employeeId
    );

    List<Attendance>
    findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Attendance>
    findByEmployeeIdAndStatusOrderByAttendanceDateDesc(
            Long employeeId,
            AttendanceStatus status
    );

    long countByAttendanceDateAndStatus(
            LocalDate attendanceDate,
            AttendanceStatus status
    );

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(
            Long companyId,
            AttendanceStatus status
    );

    @Query("""
        SELECT AVG(a.workingHours)
        FROM Attendance a
        WHERE a.company.id = :companyId
        AND a.workingHours IS NOT NULL
        """)
    Double findAverageWorkingHoursByCompanyId(
            @Param("companyId") Long companyId
    );
}