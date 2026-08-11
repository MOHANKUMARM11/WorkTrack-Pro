package com.worktrack.serviceImpl;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.response.AttendanceAnalyticsResponse;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.service.AttendanceAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceAnalyticsServiceImpl
        implements AttendanceAnalyticsService {

    private final AttendanceRepository attendanceRepository;
    private final CompanyRepository companyRepository;

    @Override
    public AttendanceAnalyticsResponse getAttendanceAnalytics(
            Long companyId) {

        companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        long totalRecords =
                attendanceRepository.countByCompanyId(companyId);

        long presentCount =
                attendanceRepository.countByCompanyIdAndStatus(
                        companyId,
                        AttendanceStatus.PRESENT);

        long absentCount =
                attendanceRepository.countByCompanyIdAndStatus(
                        companyId,
                        AttendanceStatus.ABSENT);

        long lateCount =
                attendanceRepository.countByCompanyIdAndStatus(
                        companyId,
                        AttendanceStatus.LATE);

        long halfDayCount =
                attendanceRepository.countByCompanyIdAndStatus(
                        companyId,
                        AttendanceStatus.HALF_DAY);

        Double averageWorkingHours =
                attendanceRepository
                        .findAverageWorkingHoursByCompanyId(companyId);

        return AttendanceAnalyticsResponse.builder()
                .totalRecords(totalRecords)
                .presentCount(presentCount)
                .absentCount(absentCount)
                .lateCount(lateCount)
                .halfDayCount(halfDayCount)
                .averageWorkingHours(
                        averageWorkingHours != null
                                ? averageWorkingHours
                                : 0.0)
                .build();
    }
}