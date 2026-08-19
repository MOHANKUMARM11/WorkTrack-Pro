package com.worktrack.repository;

import com.worktrack.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceLogRepository
        extends JpaRepository<AttendanceLog, Long> {

    List<AttendanceLog> findByAttendanceIdOrderByCreatedAtAsc(
            Long attendanceId);

    List<AttendanceLog> findByAttendanceEmployeeIdOrderByCreatedAtDesc(
            Long employeeId);
}