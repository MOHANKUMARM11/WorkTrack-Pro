package com.worktrack.serviceImpl;

import com.worktrack.dto.request.DesignationRequest;
import com.worktrack.dto.response.DesignationResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Designation;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.DesignationRepository;
import com.worktrack.service.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final CompanyRepository companyRepository;

    @Override
    public DesignationResponse createDesignation(DesignationRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (designationRepository.existsByCompanyIdAndTitle(request.getCompanyId(), request.getTitle())) {
            throw new IllegalArgumentException("Designation with title '" + request.getTitle() + "' already exists for this company");
        }

        Designation designation = Designation.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .company(company)
                .build();

        Designation saved = designationRepository.save(designation);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignationResponse getDesignationById(Long id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Designation not found with id: " + id));
        return mapToResponse(designation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DesignationResponse> getDesignationsByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found with id: " + companyId);
        }
        return designationRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DesignationResponse updateDesignation(Long id, DesignationRequest request) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Designation not found with id: " + id));

        if (!designation.getTitle().equalsIgnoreCase(request.getTitle()) &&
                designationRepository.existsByCompanyIdAndTitle(designation.getCompany().getId(), request.getTitle())) {
            throw new IllegalArgumentException("Designation with title '" + request.getTitle() + "' already exists for this company");
        }

        designation.setTitle(request.getTitle());
        designation.setDescription(request.getDescription());

        Designation updated = designationRepository.save(designation);
        return mapToResponse(updated);
    }

    @Override
    public void deleteDesignation(Long id) {
        if (!designationRepository.existsById(id)) {
            throw new IllegalArgumentException("Designation not found with id: " + id);
        }
        designationRepository.deleteById(id);
    }

    private DesignationResponse mapToResponse(Designation designation) {
        return DesignationResponse.builder()
                .id(designation.getId())
                .title(designation.getTitle())
                .description(designation.getDescription())
                .companyId(designation.getCompany().getId())
                .companyName(designation.getCompany().getName())
                .createdAt(designation.getCreatedAt())
                .updatedAt(designation.getUpdatedAt())
                .build();
    }
}
