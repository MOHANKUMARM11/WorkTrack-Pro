package com.worktrack.service;

import com.worktrack.dto.request.RoleRequest;
import com.worktrack.dto.response.PermissionResponse;
import com.worktrack.dto.response.RoleResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Permission;
import com.worktrack.entity.Role;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.PermissionRepository;
import com.worktrack.repository.RoleRepository;
import com.worktrack.serviceImpl.RbacServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RbacServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private RbacServiceImpl rbacService;

    private Company sampleCompany;
    private Permission samplePermission;
    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        samplePermission = Permission.builder().name("ATTENDANCE_READ").description("View attendance").build();
        ReflectionTestUtils.setField(samplePermission, "id", 10L);

        sampleRole = Role.builder()
                .name("HR_MANAGER")
                .description("HR Manager Role")
                .company(sampleCompany)
                .permissions(new HashSet<>(List.of(samplePermission)))
                .build();
        ReflectionTestUtils.setField(sampleRole, "id", 100L);
    }

    @Test
    @DisplayName("Should create custom role successfully")
    void createRole_Success() {
        RoleRequest request = RoleRequest.builder()
                .name("HR_MANAGER")
                .description("HR Manager Role")
                .companyId(1L)
                .permissionIds(List.of(10L))
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(permissionRepository.findAllById(List.of(10L))).thenReturn(List.of(samplePermission));
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);

        RoleResponse response = rbacService.createRole(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("HR_MANAGER");
        assertThat(response.getPermissions()).hasSize(1);
    }

    @Test
    @DisplayName("Should return all system permissions")
    void getAllPermissions_Success() {
        when(permissionRepository.findAll()).thenReturn(List.of(samplePermission));

        List<PermissionResponse> responses = rbacService.getAllPermissions();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("ATTENDANCE_READ");
    }
}
