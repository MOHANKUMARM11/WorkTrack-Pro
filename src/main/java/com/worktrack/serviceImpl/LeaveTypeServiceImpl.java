package com.worktrack.serviceImpl;

import com.worktrack.dto.request.LeaveTypeRequest;
import com.worktrack.dto.response.LeaveTypeResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.LeaveType;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.LeaveTypeRepository;
import com.worktrack.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final CompanyRepository companyRepository;

    @Override
    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (leaveTypeRepository.existsByCompanyIdAndCode(request.getCompanyId(), request.getCode())) {
            throw new IllegalArgumentException("Leave type with code '" + request.getCode() + "' already exists for this company");
        }

        LeaveType leaveType = LeaveType.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .daysAllowedPerYear(request.getDaysAllowedPerYear())
                .carryForwardAllowed(Boolean.TRUE.equals(request.getCarryForwardAllowed()))
                .isPaid(request.getIsPaid() == null || request.getIsPaid())
                .company(company)
                .build();

        LeaveType saved = leaveTypeRepository.save(leaveType);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveTypeResponse getLeaveTypeById(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave type not found with id: " + id));
        return mapToResponse(leaveType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getLeaveTypesByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found with id: " + companyId);
        }
        return leaveTypeRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LeaveTypeResponse updateLeaveType(Long id, LeaveTypeRequest request) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave type not found with id: " + id));

        if (!leaveType.getCode().equalsIgnoreCase(request.getCode()) &&
                leaveTypeRepository.existsByCompanyIdAndCode(leaveType.getCompany().getId(), request.getCode())) {
            throw new IllegalArgumentException("Leave type with code '" + request.getCode() + "' already exists for this company");
        }

        leaveType.setName(request.getName());
        leaveType.setCode(request.getCode().toUpperCase());
        leaveType.setDaysAllowedPerYear(request.getDaysAllowedPerYear());
        leaveType.setCarryForwardAllowed(Boolean.TRUE.equals(request.getCarryForwardAllowed()));
        leaveType.setIsPaid(request.getIsPaid() == null || request.getIsPaid());

        LeaveType updated = leaveTypeRepository.save(leaveType);
        return mapToResponse(updated);
    }

    @Override
    public void deleteLeaveType(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Leave type not found with id: " + id);
        }
        leaveTypeRepository.deleteById(id);
    }

    private LeaveTypeResponse mapToResponse(LeaveType leaveType) {
        return LeaveTypeResponse.builder()
                .id(leaveType.getId())
                .name(leaveType.getName())
                .code(leaveType.getCode())
                .daysAllowedPerYear(leaveType.getDaysAllowedPerYear())
                .carryForwardAllowed(leaveType.getCarryForwardAllowed())
                .isPaid(leaveType.getIsPaid())
                .companyId(leaveType.getCompany().getId())
                .createdAt(leaveType.getCreatedAt())
                .updatedAt(leaveType.getUpdatedAt())
                .build();
    }
}
