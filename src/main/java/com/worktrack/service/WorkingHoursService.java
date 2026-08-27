package com.worktrack.service;

import com.worktrack.dto.request.WorkingHoursRequest;
import com.worktrack.dto.response.WorkingDaysCalculationResponse;
import com.worktrack.dto.response.WorkingHoursResponse;

import java.time.LocalDate;
import java.util.List;

public interface WorkingHoursService {

    WorkingHoursResponse saveOrUpdateWorkingHours(WorkingHoursRequest request);

    List<WorkingHoursResponse> getWorkingHoursByCompanyId(Long companyId);

    WorkingDaysCalculationResponse calculateWorkingDays(Long companyId, LocalDate startDate, LocalDate endDate);
}
