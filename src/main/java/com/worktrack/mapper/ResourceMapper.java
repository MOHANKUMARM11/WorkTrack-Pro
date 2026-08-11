package com.worktrack.mapper;

import com.worktrack.dto.request.ResourceRequest;
import com.worktrack.dto.response.ResourceResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Resource;

public class ResourceMapper {

    private ResourceMapper() {
    }

    public static Resource toEntity(
            ResourceRequest request,
            Company company,
            Employee employee) {

        return Resource.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .quantity(request.getQuantity())
                .status(request.getStatus())
                .company(company)
                .employee(employee)
                .build();
    }

    public static ResourceResponse toResponse(Resource resource) {

        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .type(resource.getType())
                .quantity(resource.getQuantity())
                .status(resource.getStatus())
                .companyId(
                        resource.getCompany() != null
                                ? resource.getCompany().getId()
                                : null
                )
                .employeeId(
                        resource.getEmployee() != null
                                ? resource.getEmployee().getId()
                                : null
                )
                .build();
    }
}