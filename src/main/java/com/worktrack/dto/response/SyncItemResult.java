package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncItemResult {

    private String clientItemId;

    private String actionType;

    private String status; // SUCCESS, CONFLICT, ERROR

    private String message;

    private Long serverEntityId;
}
