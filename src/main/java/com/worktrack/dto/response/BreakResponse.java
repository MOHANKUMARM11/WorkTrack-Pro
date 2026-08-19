package com.worktrack.dto.response;

import java.time.LocalDateTime;

public record BreakResponse(

        Long id,

        Long attendanceId,

        LocalDateTime startAt,

        LocalDateTime endAt,

        Integer durationMinutes
) {
}