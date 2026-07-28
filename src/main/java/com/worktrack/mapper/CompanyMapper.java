package com.worktrack.mapper;

import com.worktrack.constants.CompanyStatus;
import com.worktrack.dto.request.CompanyRequest;
import com.worktrack.dto.response.CompanyResponse;
import com.worktrack.entity.Company;

public class CompanyMapper {

    private CompanyMapper() {
    }

    public static Company toEntity(CompanyRequest request) {

        return Company.builder()
                .name(request.getName())
                .registrationNumber(request.getRegistrationNumber())
                .industry(request.getIndustry())
                .subscriptionPlan(request.getSubscriptionPlan())
                .status(CompanyStatus.ACTIVE)
                .timezone(request.getTimezone())
                .build();
    }

    public static CompanyResponse toResponse(Company company) {

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .registrationNumber(company.getRegistrationNumber())
                .industry(company.getIndustry())
                .subscriptionPlan(company.getSubscriptionPlan())
                .status(company.getStatus())
                .timezone(company.getTimezone())
                .build();
    }
}