package com.worktrack.service;

import com.worktrack.dto.request.DesignationRequest;
import com.worktrack.dto.response.DesignationResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Designation;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.DesignationRepository;
import com.worktrack.serviceImpl.DesignationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class DesignationServiceTest {

    @Mock
    private DesignationRepository designationRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private DesignationServiceImpl designationService;

    private Company sampleCompany;
    private Designation sampleDesignation;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleDesignation = Designation.builder()
                .title("Senior Software Engineer")
                .description("Lead backend developer")
                .company(sampleCompany)
                .build();
        ReflectionTestUtils.setField(sampleDesignation, "id", 20L);
    }

    @Nested
    @DisplayName("Create Designation Tests")
    class CreateDesignationTests {

        @Test
        @DisplayName("Should successfully create a designation")
        void createDesignation_Success() {
            DesignationRequest request = DesignationRequest.builder()
                    .title("Senior Software Engineer")
                    .description("Lead backend developer")
                    .companyId(1L)
                    .build();

            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(designationRepository.existsByCompanyIdAndTitle(1L, "Senior Software Engineer")).thenReturn(false);
            when(designationRepository.save(any(Designation.class))).thenReturn(sampleDesignation);

            DesignationResponse response = designationService.createDesignation(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(20L);
            assertThat(response.getTitle()).isEqualTo("Senior Software Engineer");
            assertThat(response.getCompanyId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw CompanyNotFoundException when company ID invalid")
        void createDesignation_CompanyNotFound() {
            DesignationRequest request = DesignationRequest.builder().title("Engineer").companyId(999L).build();

            when(companyRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> designationService.createDesignation(request))
                    .isInstanceOf(CompanyNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when designation title duplicate for company")
        void createDesignation_DuplicateTitle() {
            DesignationRequest request = DesignationRequest.builder().title("Senior Software Engineer").companyId(1L).build();

            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(designationRepository.existsByCompanyIdAndTitle(1L, "Senior Software Engineer")).thenReturn(true);

            assertThatThrownBy(() -> designationService.createDesignation(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("Get Designation Tests")
    class GetDesignationTests {

        @Test
        @DisplayName("Should return designation response when designation ID exists")
        void getDesignationById_Success() {
            when(designationRepository.findById(20L)).thenReturn(Optional.of(sampleDesignation));

            DesignationResponse response = designationService.getDesignationById(20L);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Senior Software Engineer");
        }

        @Test
        @DisplayName("Should return list of designations for company")
        void getDesignationsByCompanyId_Success() {
            when(companyRepository.existsById(1L)).thenReturn(true);
            when(designationRepository.findByCompanyId(1L)).thenReturn(List.of(sampleDesignation));

            List<DesignationResponse> responses = designationService.getDesignationsByCompanyId(1L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getTitle()).isEqualTo("Senior Software Engineer");
        }
    }
}
