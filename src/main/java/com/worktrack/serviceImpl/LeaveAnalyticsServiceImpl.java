package com.worktrack.serviceImpl;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.dto.response.LeaveAnalyticsResponse;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.service.LeaveAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveAnalyticsServiceImpl
        implements LeaveAnalyticsService {

    private final LeaveRepository leaveRepository;
    private final CompanyRepository companyRepository;

    @Override
    public LeaveAnalyticsResponse getLeaveAnalytics(
            Long companyId) {

        companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        long totalLeaves =
                leaveRepository.countByCompanyId(companyId);

        long pendingCount =
                leaveRepository.countByCompanyIdAndStatus(
                        companyId,
                        LeaveStatus.PENDING);

        long approvedCount =
                leaveRepository.countByCompanyIdAndStatus(
                        companyId,
                        LeaveStatus.APPROVED);

        long rejectedCount =
                leaveRepository.countByCompanyIdAndStatus(
                        companyId,
                        LeaveStatus.REJECTED);

        Integer totalLeaveDays =
                leaveRepository.sumTotalLeaveDaysByCompanyId(companyId);

        return LeaveAnalyticsResponse.builder()
                .totalLeaves(totalLeaves)
                .pendingCount(pendingCount)
                .approvedCount(approvedCount)
                .rejectedCount(rejectedCount)
                .totalLeaveDays(
                        totalLeaveDays != null
                                ? totalLeaveDays
                                : 0)
                .build();
    }
}