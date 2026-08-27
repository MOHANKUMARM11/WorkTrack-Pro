package com.worktrack.serviceImpl;

import com.worktrack.dto.request.WorkingHoursRequest;
import com.worktrack.dto.response.WorkingDaysCalculationResponse;
import com.worktrack.dto.response.WorkingHoursResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Holiday;
import com.worktrack.entity.WorkingHours;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.HolidayRepository;
import com.worktrack.repository.WorkingHoursRepository;
import com.worktrack.service.WorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkingHoursServiceImpl implements WorkingHoursService {

    private final WorkingHoursRepository workingHoursRepository;
    private final CompanyRepository companyRepository;
    private final HolidayRepository holidayRepository;

    @Override
    public WorkingHoursResponse saveOrUpdateWorkingHours(WorkingHoursRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        String dayUpper = request.getDayOfWeek().toUpperCase();

        Optional<WorkingHours> existing = workingHoursRepository.findByCompanyIdAndDayOfWeek(company.getId(), dayUpper);
        WorkingHours workingHours;

        if (existing.isPresent()) {
            workingHours = existing.get();
            workingHours.setStartTime(request.getStartTime());
            workingHours.setEndTime(request.getEndTime());
            if (request.getIsWorkingDay() != null) {
                workingHours.setIsWorkingDay(request.getIsWorkingDay());
            }
        } else {
            workingHours = WorkingHours.builder()
                    .company(company)
                    .dayOfWeek(dayUpper)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .isWorkingDay(request.getIsWorkingDay() != null ? request.getIsWorkingDay() : true)
                    .build();
        }

        return mapToResponse(workingHoursRepository.save(workingHours));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkingHoursResponse> getWorkingHoursByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return workingHoursRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkingDaysCalculationResponse calculateWorkingDays(Long companyId, LocalDate startDate, LocalDate endDate) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        Set<LocalDate> companyHolidays = holidayRepository.findByCompanyIdAndHolidayDateBetween(companyId, startDate, endDate)
                .stream()
                .map(Holiday::getHolidayDate)
                .collect(Collectors.toSet());

        Map<String, WorkingHours> scheduleMap = workingHoursRepository.findByCompanyId(companyId).stream()
                .collect(Collectors.toMap(wh -> wh.getDayOfWeek().toUpperCase(), wh -> wh));

        int calendarDays = 0;
        int workingDays = 0;
        int holidaysCount = 0;
        int offDaysCount = 0;
        double totalHours = 0.0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            calendarDays++;
            String dayOfWeekName = date.getDayOfWeek().name();

            if (companyHolidays.contains(date)) {
                holidaysCount++;
                continue;
            }

            WorkingHours schedule = scheduleMap.get(dayOfWeekName);
            boolean isWorkingDay = (schedule != null) ? schedule.getIsWorkingDay() : (date.getDayOfWeek().getValue() <= 5);

            if (!isWorkingDay) {
                offDaysCount++;
                continue;
            }

            workingDays++;
            LocalTime start = (schedule != null) ? schedule.getStartTime() : LocalTime.of(9, 0);
            LocalTime end = (schedule != null) ? schedule.getEndTime() : LocalTime.of(17, 0);
            double dayHours = Duration.between(start, end).toMinutes() / 60.0;
            totalHours += Math.max(0.0, dayHours);
        }

        return WorkingDaysCalculationResponse.builder()
                .companyId(companyId)
                .startDate(startDate)
                .endDate(endDate)
                .totalCalendarDays(calendarDays)
                .netWorkingDays(workingDays)
                .holidaysCount(holidaysCount)
                .offDaysCount(offDaysCount)
                .totalWorkingHours(totalHours)
                .build();
    }

    private WorkingHoursResponse mapToResponse(WorkingHours workingHours) {
        return WorkingHoursResponse.builder()
                .id(workingHours.getId())
                .companyId(workingHours.getCompany().getId())
                .dayOfWeek(workingHours.getDayOfWeek())
                .startTime(workingHours.getStartTime())
                .endTime(workingHours.getEndTime())
                .isWorkingDay(workingHours.getIsWorkingDay())
                .createdAt(workingHours.getCreatedAt())
                .updatedAt(workingHours.getUpdatedAt())
                .build();
    }
}
