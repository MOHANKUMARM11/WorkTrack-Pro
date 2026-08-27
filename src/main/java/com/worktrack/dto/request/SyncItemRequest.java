package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncItemRequest {

    @NotBlank(message = "Client item ID is required")
    private String clientItemId;

    @NotBlank(message = "Action type is required")
    private String actionType; // ATTENDANCE_CHECKIN, BREAK_START, BREAK_END, TASK_STATUS_UPDATE

    private Long entityId;

    private Long clientTimestamp;

    private Map<String, Object> payload;
}
