package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentResponse {

    private Long id;

    private Long shiftId;

    private String shiftName;

    private Long employeeId;

    private String employeeName;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;
}
