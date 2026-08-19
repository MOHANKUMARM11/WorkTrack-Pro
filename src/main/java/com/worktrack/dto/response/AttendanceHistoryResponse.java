package com.worktrack.dto.response;

import com.worktrack.constants.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceHistoryResponse(

        Long attendanceId,

        Long employeeId,

        Long companyId,

        LocalDate attendanceDate,

        LocalTime checkIn,

        LocalTime checkOut,

        Double workingHours,

        AttendanceStatus status
) {
}