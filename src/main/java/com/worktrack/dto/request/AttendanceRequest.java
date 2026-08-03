package com.worktrack.dto.request;

import com.worktrack.constants.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceRequest {

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Check-in time is required")
    private LocalTime checkIn;

    private LocalTime checkOut;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}