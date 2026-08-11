package com.worktrack.serviceImpl;

import com.worktrack.constants.ResourceStatus;
import com.worktrack.dto.request.ResourceRequest;
import com.worktrack.dto.response.ResourceResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Resource;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.exception.custom.DuplicateResourceException;
import com.worktrack.mapper.ResourceMapper;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.ResourceRepository;
import com.worktrack.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ResourceResponse createResource(ResourceRequest request) {

        if (resourceRepository.existsByNameAndCompanyId(
                request.getName(),
                request.getCompanyId())) {

            throw new DuplicateResourceException(
                    "Resource already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        Employee employee = null;

        if (request.getEmployeeId() != null) {
            employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() ->
                            new EmployeeNotFoundException("Employee not found"));
        }

        Resource resource =
                ResourceMapper.toEntity(request, company, employee);

        return ResourceMapper.toResponse(
                resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse getResourceById(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        return ResourceMapper.toResponse(resource);
    }

    @Override
    public List<ResourceResponse> getAllResources() {

        return resourceRepository.findAll()
                .stream()
                .map(ResourceMapper::toResponse)
                .toList();
    }

    @Override
    public ResourceResponse updateResource(
            Long id,
            ResourceRequest request) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        resourceRepository.findByNameAndCompanyId(
                        request.getName(),
                        request.getCompanyId())
                .ifPresent(existingResource -> {
                    if (!existingResource.getId().equals(resource.getId())) {
                        throw new DuplicateResourceException(
                                "Resource already exists");
                    }
                });

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        Employee employee = null;

        if (request.getEmployeeId() != null) {
            employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() ->
                            new EmployeeNotFoundException("Employee not found"));
        }

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setQuantity(request.getQuantity());
        resource.setStatus(request.getStatus());
        resource.setCompany(company);
        resource.setEmployee(employee);

        return ResourceMapper.toResponse(
                resourceRepository.save(resource));
    }

    @Override
    public ResourceResponse updateResourceStatus(
            Long id,
            ResourceStatus status) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        resource.setStatus(status);

        return ResourceMapper.toResponse(
                resourceRepository.save(resource));
    }

    @Override
    public void deleteResource(Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found"));

        resourceRepository.delete(resource);
    }
}