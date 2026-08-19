package com.worktrack.dto.response;

import com.worktrack.constants.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceTodayResponse(

        Long attendanceId,

        Long employeeId,

        LocalDate attendanceDate,

        LocalTime checkIn,

        LocalTime checkOut,

        Double workingHours,

        AttendanceStatus status,

        Boolean checkedIn,

        Boolean checkedOut
) {
}