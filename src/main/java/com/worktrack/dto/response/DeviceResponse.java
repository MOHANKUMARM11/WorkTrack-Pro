package com.worktrack.dto.response;

import java.time.LocalDateTime;

public record DeviceResponse(
        Long id,
        String deviceId,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime revokedAt
) {
}
