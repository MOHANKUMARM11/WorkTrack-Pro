package com.worktrack.service;

import com.worktrack.dto.request.RoleRequest;
import com.worktrack.dto.response.PermissionResponse;
import com.worktrack.dto.response.RoleResponse;

import java.util.List;

public interface RbacService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse getRoleById(Long id);

    List<RoleResponse> getRolesByCompanyId(Long companyId);

    List<RoleResponse> getAllRoles();

    RoleResponse assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    List<PermissionResponse> getAllPermissions();

    void deleteRole(Long id);
}
