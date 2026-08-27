package com.worktrack.dto.request;

import jakarta.validation.constraints.*;

public record AttendanceCheckOutRequest(

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Invalid latitude")
        @DecimalMax(value = "90.0", message = "Invalid latitude")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Invalid longitude")
        @DecimalMax(value = "180.0", message = "Invalid longitude")
        Double longitude,

        @NotNull(message = "GPS accuracy is required")
        @PositiveOrZero(message = "Accuracy cannot be negative")
        Double accuracyM,

        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotBlank(message = "Device secret is required")
        String deviceSecret,

        String beaconId,

        String wifiBssid
) {
}