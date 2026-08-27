package com.worktrack.service;

import com.worktrack.dto.request.BranchRequest;
import com.worktrack.dto.response.BranchResponse;

import java.util.List;

public interface BranchService {

    BranchResponse createBranch(BranchRequest request);

    BranchResponse getBranchById(Long id);

    List<BranchResponse> getBranchesByCompanyId(Long companyId);

    BranchResponse updateBranch(Long id, BranchRequest request);

    void deleteBranch(Long id);
}
