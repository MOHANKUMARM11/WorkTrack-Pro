package com.worktrack.serviceImpl;

import com.worktrack.dto.request.RoleRequest;
import com.worktrack.dto.response.PermissionResponse;
import com.worktrack.dto.response.RoleResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Permission;
import com.worktrack.entity.Role;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.PermissionRepository;
import com.worktrack.repository.RoleRepository;
import com.worktrack.service.RbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RbacServiceImpl implements RbacService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CompanyRepository companyRepository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        Company company = null;
        if (request.getCompanyId() != null) {
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
        }

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            permissions.addAll(permissionRepository.findAllById(request.getPermissionIds()));
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .company(company)
                .permissions(permissions)
                .build();

        return mapToRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getRolesByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return roleRepository.findByCompanyId(companyId).stream()
                .map(this::mapToRoleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToRoleResponse)
                .toList();
    }

    @Override
    public RoleResponse assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.getPermissions().addAll(permissions);

        return mapToRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToPermissionResponse)
                .toList();
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        roleRepository.delete(role);
    }

    private RoleResponse mapToRoleResponse(Role role) {
        List<PermissionResponse> permissionResponses = role.getPermissions().stream()
                .map(this::mapToPermissionResponse)
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .companyId(role.getCompany() != null ? role.getCompany().getId() : null)
                .permissions(permissionResponses)
                .createdAt(role.getCreatedAt())
                .build();
    }

    private PermissionResponse mapToPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .build();
    }
}
