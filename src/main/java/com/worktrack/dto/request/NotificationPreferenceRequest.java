package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationPreferenceRequest {

    @NotBlank(message = "Channel is required")
    private String channel;

    @NotNull(message = "Enabled value is required")
    private Boolean enabled;
}