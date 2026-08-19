package com.worktrack.dto.response;

import com.worktrack.constants.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AttendanceCheckInResponse(

        Long attendanceId,

        Long employeeId,

        Long companyId,

        LocalDate attendanceDate,

        LocalTime checkIn,

        LocalTime checkOut,

        Double workingHours,

        AttendanceStatus status,

        String source,

        Double distanceFromOfficeM,

        Boolean withinGeofence,

        LocalDateTime recordedAt
) {
}