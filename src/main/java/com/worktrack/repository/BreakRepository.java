package com.worktrack.repository;

import com.worktrack.entity.Break;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BreakRepository
        extends JpaRepository<Break, Long> {

    List<Break> findByAttendanceIdOrderByStartAtAsc(
            Long attendanceId);

    Optional<Break> findFirstByAttendanceIdAndEndAtIsNullOrderByStartAtDesc(
            Long attendanceId);
}