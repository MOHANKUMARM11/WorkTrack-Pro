package com.worktrack.serviceImpl;

import com.worktrack.dto.request.BranchRequest;
import com.worktrack.dto.response.BranchResponse;
import com.worktrack.entity.Branch;
import com.worktrack.entity.Company;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.BranchRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;

    @Override
    public BranchResponse createBranch(BranchRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (branchRepository.existsByCompanyIdAndName(request.getCompanyId(), request.getName())) {
            throw new IllegalArgumentException("Branch with name '" + request.getName() + "' already exists for this company");
        }

        Branch branch = Branch.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .company(company)
                .build();

        Branch saved = branchRepository.save(branch);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with id: " + id));
        return mapToResponse(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getBranchesByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found with id: " + companyId);
        }
        return branchRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BranchResponse updateBranch(Long id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found with id: " + id));

        if (!branch.getName().equalsIgnoreCase(request.getName()) &&
                branchRepository.existsByCompanyIdAndName(branch.getCompany().getId(), request.getName())) {
            throw new IllegalArgumentException("Branch with name '" + request.getName() + "' already exists for this company");
        }

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setCountry(request.getCountry());

        Branch updated = branchRepository.save(branch);
        return mapToResponse(updated);
    }

    @Override
    public void deleteBranch(Long id) {
        if (!branchRepository.existsById(id)) {
            throw new IllegalArgumentException("Branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .city(branch.getCity())
                .country(branch.getCountry())
                .companyId(branch.getCompany().getId())
                .companyName(branch.getCompany().getName())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}
