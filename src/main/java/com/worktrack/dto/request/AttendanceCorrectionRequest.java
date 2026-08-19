package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;

public record AttendanceCorrectionRequest(

        LocalTime checkIn,

        LocalTime checkOut,

        @NotBlank(message = "Correction reason is required")
        String reason,

        String deviceSignature
) {
}