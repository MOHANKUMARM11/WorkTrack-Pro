package com.worktrack.service;

import com.worktrack.constants.ResourceStatus;
import com.worktrack.dto.request.ResourceRequest;
import com.worktrack.dto.response.ResourceResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Resource;
import com.worktrack.exception.custom.DuplicateResourceException;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.ResourceRepository;
import com.worktrack.serviceImpl.ResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Company sampleCompany;
    private Employee sampleEmployee;
    private Resource sampleResource;
    private ResourceRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Corp").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleEmployee = Employee.builder().build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 10L);

        sampleResource = Resource.builder()
                .name("MacBook Pro M3")
                .description("Developer laptop")
                .type("HARDWARE")
                .quantity(1)
                .status(ResourceStatus.ASSIGNED)
                .company(sampleCompany)
                .employee(sampleEmployee)
                .build();
        ReflectionTestUtils.setField(sampleResource, "id", 50L);

        sampleRequest = new ResourceRequest();
        sampleRequest.setName("MacBook Pro M3");
        sampleRequest.setDescription("Developer laptop");
        sampleRequest.setType("HARDWARE");
        sampleRequest.setQuantity(1);
        sampleRequest.setStatus(ResourceStatus.ASSIGNED);
        sampleRequest.setCompanyId(1L);
        sampleRequest.setEmployeeId(10L);
    }

    @Test
    @DisplayName("Should create resource successfully")
    void createResource_Success() {
        when(resourceRepository.existsByNameAndCompanyId("MacBook Pro M3", 1L)).thenReturn(false);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(sampleEmployee));
        when(resourceRepository.save(any(Resource.class))).thenReturn(sampleResource);

        ResourceResponse response = resourceService.createResource(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(50L);
        assertThat(response.getName()).isEqualTo("MacBook Pro M3");
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when resource exists")
    void createResource_Duplicate_ThrowsException() {
        when(resourceRepository.existsByNameAndCompanyId("MacBook Pro M3", 1L)).thenReturn(true);

        assertThatThrownBy(() -> resourceService.createResource(sampleRequest))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Should get resource by ID")
    void getResourceById_Success() {
        when(resourceRepository.findById(50L)).thenReturn(Optional.of(sampleResource));

        ResourceResponse response = resourceService.getResourceById(50L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when resource ID not found")
    void getResourceById_NotFound_ThrowsException() {
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.getResourceById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should get all resources")
    void getAllResources_Success() {
        when(resourceRepository.findAll()).thenReturn(List.of(sampleResource));

        List<ResourceResponse> response = resourceService.getAllResources();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("MacBook Pro M3");
    }

    @Test
    @DisplayName("Should update resource status")
    void updateResourceStatus_Success() {
        when(resourceRepository.findById(50L)).thenReturn(Optional.of(sampleResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(sampleResource);

        ResourceResponse response = resourceService.updateResourceStatus(50L, ResourceStatus.AVAILABLE);

        assertThat(response).isNotNull();
        verify(resourceRepository).save(sampleResource);
    }

    @Test
    @DisplayName("Should delete resource successfully")
    void deleteResource_Success() {
        when(resourceRepository.findById(50L)).thenReturn(Optional.of(sampleResource));

        resourceService.deleteResource(50L);

        verify(resourceRepository).delete(sampleResource);
    }
}
