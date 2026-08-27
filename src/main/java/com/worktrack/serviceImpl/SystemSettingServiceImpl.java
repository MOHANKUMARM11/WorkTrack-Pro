package com.worktrack.serviceImpl;

import com.worktrack.dto.request.SystemSettingRequest;
import com.worktrack.dto.response.SystemSettingResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.SystemSetting;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.SystemSettingRepository;
import com.worktrack.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final CompanyRepository companyRepository;

    @Override
    public SystemSettingResponse saveOrUpdateSetting(SystemSettingRequest request) {
        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        }

        Optional<SystemSetting> existing = (company != null) ?
                systemSettingRepository.findByCompanyIdAndKey(company.getId(), request.getKey()) :
                systemSettingRepository.findByKeyAndCompanyIdIsNull(request.getKey());

        SystemSetting setting;
        if (existing.isPresent()) {
            setting = existing.get();
            setting.setValue(request.getValue());
            if (request.getCategory() != null) setting.setCategory(request.getCategory());
            if (request.getDescription() != null) setting.setDescription(request.getDescription());
        } else {
            setting = SystemSetting.builder()
                    .key(request.getKey())
                    .value(request.getValue())
                    .category(request.getCategory() != null ? request.getCategory() : "GENERAL")
                    .description(request.getDescription())
                    .company(company)
                    .build();
        }

        return mapToResponse(systemSettingRepository.save(setting));
    }

    @Override
    @Transactional(readOnly = true)
    public SystemSettingResponse getSettingByKeyAndCompany(String key, Long companyId) {
        Optional<SystemSetting> setting = (companyId != null) ?
                systemSettingRepository.findByCompanyIdAndKey(companyId, key) :
                systemSettingRepository.findByKeyAndCompanyIdIsNull(key);

        return setting.map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found for key: " + key));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingResponse> getSettingsByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return systemSettingRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingResponse> getAllSettings() {
        return systemSettingRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteSetting(Long id) {
        SystemSetting setting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found with id: " + id));
        systemSettingRepository.delete(setting);
    }

    private SystemSettingResponse mapToResponse(SystemSetting setting) {
        return SystemSettingResponse.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .category(setting.getCategory())
                .description(setting.getDescription())
                .companyId(setting.getCompany() != null ? setting.getCompany().getId() : null)
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
