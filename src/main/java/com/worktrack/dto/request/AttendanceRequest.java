package com.worktrack.dto.request;

import com.worktrack.constants.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceRequest {

    @NotNull
    private LocalDate attendanceDate;

    @NotNull
    private LocalTime checkIn;

    private LocalTime checkOut;

    @NotNull
    private AttendanceStatus status;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long companyId;
}