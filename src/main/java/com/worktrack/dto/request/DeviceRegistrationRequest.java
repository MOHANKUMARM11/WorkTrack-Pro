package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeviceRegistrationRequest(

        @NotBlank(message = "Device ID is required")
        String deviceId,

        @NotBlank(message = "Device secret is required")
        String deviceSecret
) {
}