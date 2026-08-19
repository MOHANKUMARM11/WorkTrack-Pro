package com.worktrack.repository;

import com.worktrack.entity.GpsLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GpsLocationRepository
        extends JpaRepository<GpsLocation, Long> {

    List<GpsLocation> findByAttendanceLogIdOrderByRecordedAtAsc(
            Long attendanceLogId);
}