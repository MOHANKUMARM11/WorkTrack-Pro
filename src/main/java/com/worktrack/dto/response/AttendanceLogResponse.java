package com.worktrack.dto.response;

import com.worktrack.constants.AttendanceEventType;

import java.time.LocalDateTime;

public record AttendanceLogResponse(

        Long id,

        Long attendanceId,

        AttendanceEventType eventType,

        Double latitude,

        Double longitude,

        Double accuracyM,

        String source,

        String beaconId,

        String wifiBssid,

        String manualNote,

        String photoUrl,

        LocalDateTime createdAt
) {
}