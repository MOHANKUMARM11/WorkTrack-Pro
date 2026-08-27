package com.worktrack.service;

import com.worktrack.dto.request.BranchRequest;
import com.worktrack.dto.response.BranchResponse;
import com.worktrack.entity.Branch;
import com.worktrack.entity.Company;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.BranchRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.serviceImpl.BranchServiceImpl;
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
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private BranchServiceImpl branchService;

    private Company sampleCompany;
    private Branch sampleBranch;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleBranch = Branch.builder()
                .name("Downtown Branch")
                .address("123 Main St")
                .city("Metropolis")
                .country("USA")
                .company(sampleCompany)
                .build();
        ReflectionTestUtils.setField(sampleBranch, "id", 10L);
    }

    @Nested
    @DisplayName("Create Branch Tests")
    class CreateBranchTests {

        @Test
        @DisplayName("Should successfully create a branch")
        void createBranch_Success() {
            BranchRequest request = BranchRequest.builder()
                    .name("Downtown Branch")
                    .address("123 Main St")
                    .city("Metropolis")
                    .country("USA")
                    .companyId(1L)
                    .build();

            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(branchRepository.existsByCompanyIdAndName(1L, "Downtown Branch")).thenReturn(false);
            when(branchRepository.save(any(Branch.class))).thenReturn(sampleBranch);

            BranchResponse response = branchService.createBranch(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(10L);
            assertThat(response.getName()).isEqualTo("Downtown Branch");
            assertThat(response.getCompanyId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw CompanyNotFoundException when company ID invalid")
        void createBranch_CompanyNotFound() {
            BranchRequest request = BranchRequest.builder().name("Branch 1").companyId(999L).build();

            when(companyRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> branchService.createBranch(request))
                    .isInstanceOf(CompanyNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when branch name duplicate for company")
        void createBranch_DuplicateName() {
            BranchRequest request = BranchRequest.builder().name("Downtown Branch").companyId(1L).build();

            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(branchRepository.existsByCompanyIdAndName(1L, "Downtown Branch")).thenReturn(true);

            assertThatThrownBy(() -> branchService.createBranch(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("Get Branch Tests")
    class GetBranchTests {

        @Test
        @DisplayName("Should return branch response when branch ID exists")
        void getBranchById_Success() {
            when(branchRepository.findById(10L)).thenReturn(Optional.of(sampleBranch));

            BranchResponse response = branchService.getBranchById(10L);

            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("Downtown Branch");
        }

        @Test
        @DisplayName("Should return list of branches for company")
        void getBranchesByCompanyId_Success() {
            when(companyRepository.existsById(1L)).thenReturn(true);
            when(branchRepository.findByCompanyId(1L)).thenReturn(List.of(sampleBranch));

            List<BranchResponse> responses = branchService.getBranchesByCompanyId(1L);

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getName()).isEqualTo("Downtown Branch");
        }
    }
}
