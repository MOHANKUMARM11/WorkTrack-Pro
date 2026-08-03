package com.worktrack.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private boolean success;

    private int status;

    private String message;

    private LocalDateTime timestamp;

    // For validation errors (optional)
    private Map<String, String> errors;

}