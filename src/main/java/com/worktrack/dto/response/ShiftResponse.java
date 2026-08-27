package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftResponse {

    private Long id;

    private String name;

    private Long companyId;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer gracePeriodMinutes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
