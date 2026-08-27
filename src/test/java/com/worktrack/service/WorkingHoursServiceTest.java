package com.worktrack.service;

import com.worktrack.dto.request.WorkingHoursRequest;
import com.worktrack.dto.response.WorkingDaysCalculationResponse;
import com.worktrack.dto.response.WorkingHoursResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.WorkingHours;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.HolidayRepository;
import com.worktrack.repository.WorkingHoursRepository;
import com.worktrack.serviceImpl.WorkingHoursServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkingHoursServiceTest {

    @Mock
    private WorkingHoursRepository workingHoursRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private WorkingHoursServiceImpl workingHoursService;

    private Company sampleCompany;
    private WorkingHours sampleWorkingHours;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleWorkingHours = WorkingHours.builder()
                .company(sampleCompany)
                .dayOfWeek("MONDAY")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isWorkingDay(true)
                .build();
        ReflectionTestUtils.setField(sampleWorkingHours, "id", 100L);
    }

    @Test
    @DisplayName("Should save or update working hours schedule")
    void saveOrUpdateWorkingHours_Success() {
        WorkingHoursRequest request = WorkingHoursRequest.builder()
                .companyId(1L)
                .dayOfWeek("MONDAY")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isWorkingDay(true)
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(workingHoursRepository.findByCompanyIdAndDayOfWeek(1L, "MONDAY")).thenReturn(Optional.empty());
        when(workingHoursRepository.save(any(WorkingHours.class))).thenReturn(sampleWorkingHours);

        WorkingHoursResponse response = workingHoursService.saveOrUpdateWorkingHours(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getDayOfWeek()).isEqualTo("MONDAY");
    }

    @Test
    @DisplayName("Should calculate net working days and hours between two dates")
    void calculateWorkingDays_Success() {
        LocalDate startDate = LocalDate.of(2026, 9, 1); // Tuesday
        LocalDate endDate = LocalDate.of(2026, 9, 7);   // Monday (7 days)

        when(companyRepository.existsById(1L)).thenReturn(true);
        when(holidayRepository.findByCompanyIdAndHolidayDateBetween(1L, startDate, endDate)).thenReturn(List.of());
        when(workingHoursRepository.findByCompanyId(1L)).thenReturn(List.of(sampleWorkingHours));

        WorkingDaysCalculationResponse calculation = workingHoursService.calculateWorkingDays(1L, startDate, endDate);

        assertThat(calculation).isNotNull();
        assertThat(calculation.getTotalCalendarDays()).isEqualTo(7);
        assertThat(calculation.getNetWorkingDays()).isEqualTo(5);
        assertThat(calculation.getOffDaysCount()).isEqualTo(2);
    }
}
