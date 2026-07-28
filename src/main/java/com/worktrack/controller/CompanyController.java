package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.request.CompanyRequest;
import com.worktrack.dto.request.CompanyStatusRequest;
import com.worktrack.dto.response.CompanyResponse;
import com.worktrack.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request) {

        CompanyResponse response = companyService.createCompany(request);

        return ApiResponse.<CompanyResponse>builder()
                .success(true)
                .message("Company created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getCompanyById(
            @PathVariable Long id) {

        CompanyResponse response = companyService.getCompanyById(id);

        return ApiResponse.<CompanyResponse>builder()
                .success(true)
                .message("Company retrieved successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequest request) {

        CompanyResponse response = companyService.updateCompany(id, request);
        System.out.println("===== UPDATE COMPANY CALLED =====");
        return ApiResponse.<CompanyResponse>builder()
                .success(true)
                .message("Company updated successfully")
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<CompanyResponse> updateCompanyStatus(
            @PathVariable Long id,
            @Valid @RequestBody CompanyStatusRequest request) {

        CompanyResponse response =
                companyService.updateCompanyStatus(id, request.getStatus());

        return ApiResponse.<CompanyResponse>builder()
                .success(true)
                .message("Company status updated successfully")
                .data(response)
                .build();
    }

}