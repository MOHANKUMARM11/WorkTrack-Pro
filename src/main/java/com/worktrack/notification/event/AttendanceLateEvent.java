package com.worktrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AttendanceLateEvent {

    private final Long attendanceId;
    private final Long employeeId;
    private final LocalDate attendanceDate;
}