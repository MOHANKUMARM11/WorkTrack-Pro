package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDaysCalculationResponse {

    private Long companyId;

    private LocalDate startDate;

    private LocalDate endDate;

    private int totalCalendarDays;

    private int netWorkingDays;

    private int holidaysCount;

    private int offDaysCount;

    private double totalWorkingHours;
}
