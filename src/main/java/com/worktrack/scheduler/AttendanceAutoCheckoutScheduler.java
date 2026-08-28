package com.worktrack.scheduler;

import com.worktrack.entity.Attendance;
import com.worktrack.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceAutoCheckoutScheduler {

    private final AttendanceRepository attendanceRepository;

    @Scheduled(cron = "0 0 2 * * *") // Runs daily at 02:00 AM
    @Transactional
    public int processAutoCheckouts() {
        LocalDate today = LocalDate.now();
        log.info("Running AttendanceAutoCheckoutScheduler for records prior to {}", today);

        List<Attendance> unclosedRecords = attendanceRepository.findByCheckOutIsNullAndAttendanceDateBefore(today);
        int autoCheckoutCount = 0;

        for (Attendance attendance : unclosedRecords) {
            LocalTime autoCheckOutTime = LocalTime.of(18, 0); // Default 06:00 PM auto check-out
            if (attendance.getCheckIn() != null && attendance.getCheckIn().isAfter(autoCheckOutTime)) {
                autoCheckOutTime = attendance.getCheckIn().plusHours(8);
            }

            attendance.setCheckOut(autoCheckOutTime);
            if (attendance.getCheckIn() != null) {
                double hours = Duration.between(attendance.getCheckIn(), autoCheckOutTime).toMinutes() / 60.0;
                attendance.setWorkingHours(Math.max(0.0, hours));
            }

            attendanceRepository.save(attendance);
            autoCheckoutCount++;
            log.debug("Auto checked-out attendance record ID: {} for employee ID: {}", attendance.getId(), attendance.getEmployee().getId());
        }

        log.info("AttendanceAutoCheckoutScheduler completed. Auto checked-out {} records.", autoCheckoutCount);
        return autoCheckoutCount;
    }
}
