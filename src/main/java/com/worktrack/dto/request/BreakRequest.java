package com.worktrack.dto.request;

import jakarta.validation.constraints.NotNull;

public record BreakRequest(

        @NotNull(message = "Attendance ID is required")
        Long attendanceId
) {
}