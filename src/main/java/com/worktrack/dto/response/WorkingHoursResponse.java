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
public class WorkingHoursResponse {

    private Long id;

    private Long companyId;

    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean isWorkingDay;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
