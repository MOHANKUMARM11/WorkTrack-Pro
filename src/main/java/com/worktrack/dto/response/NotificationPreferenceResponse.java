package com.worktrack.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPreferenceResponse {

    private Long id;

    private String channel;

    private Boolean enabled;
}