package com.worktrack.service;

import com.worktrack.dto.request.CompanyRequest;
import com.worktrack.dto.response.CompanyResponse;
import com.worktrack.constants.CompanyStatus;

public interface CompanyService {

    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse getCompanyById(Long id);
    CompanyResponse updateCompany(Long id, CompanyRequest request);
    CompanyResponse updateCompanyStatus(Long id, CompanyStatus status);

}