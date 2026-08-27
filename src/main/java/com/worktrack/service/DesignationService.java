package com.worktrack.service;

import com.worktrack.dto.request.DesignationRequest;
import com.worktrack.dto.response.DesignationResponse;

import java.util.List;

public interface DesignationService {

    DesignationResponse createDesignation(DesignationRequest request);

    DesignationResponse getDesignationById(Long id);

    List<DesignationResponse> getDesignationsByCompanyId(Long companyId);

    DesignationResponse updateDesignation(Long id, DesignationRequest request);

    void deleteDesignation(Long id);
}
