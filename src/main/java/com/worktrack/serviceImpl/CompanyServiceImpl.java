package com.worktrack.serviceImpl;

import com.worktrack.constants.CompanyStatus;
import com.worktrack.dto.request.CompanyRequest;
import com.worktrack.dto.response.CompanyResponse;
import com.worktrack.entity.Company;
import com.worktrack.mapper.CompanyMapper;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyResponse createCompany(CompanyRequest request) {

        if (companyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Company name already exists");
        }

        if (companyRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new RuntimeException("Registration number already exists");
        }

        Company company = CompanyMapper.toEntity(request);

        Company savedCompany = companyRepository.save(company);

        return CompanyMapper.toResponse(savedCompany);
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return CompanyMapper.toResponse(company);
    }

    @Override
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        companyRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(company.getId())) {
                        throw new RuntimeException("Company name already exists");
                    }
                });

        companyRepository.findByRegistrationNumber(request.getRegistrationNumber())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(company.getId())) {
                        throw new RuntimeException("Registration number already exists");
                    }
                });

        company.setName(request.getName());
        company.setRegistrationNumber(request.getRegistrationNumber());
        company.setIndustry(request.getIndustry());
        company.setSubscriptionPlan(request.getSubscriptionPlan());
        company.setTimezone(request.getTimezone());

        Company updatedCompany = companyRepository.save(company);

        return CompanyMapper.toResponse(updatedCompany);
    }

    @Override
    public CompanyResponse updateCompanyStatus(Long id, CompanyStatus status) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setStatus(status);

        Company updatedCompany = companyRepository.save(company);

        return CompanyMapper.toResponse(updatedCompany);
    }
}