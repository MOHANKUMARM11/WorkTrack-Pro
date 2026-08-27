package com.worktrack.controller;

import com.worktrack.dto.request.RoleRequest;
import com.worktrack.dto.response.PermissionResponse;
import com.worktrack.dto.response.RoleResponse;
import com.worktrack.service.RbacService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rbac")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RbacController {

    private final RbacService rbacService;

    @PostMapping("/roles")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return new ResponseEntity<>(rbacService.createRole(request), HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(rbacService.getAllRoles());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(rbacService.getRoleById(id));
    }

    @GetMapping("/roles/company/{companyId}")
    public ResponseEntity<List<RoleResponse>> getRolesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(rbacService.getRolesByCompanyId(companyId));
    }

    @PostMapping("/roles/{id}/permissions")
    public ResponseEntity<RoleResponse> assignPermissionsToRole(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        return ResponseEntity.ok(rbacService.assignPermissionsToRole(id, permissionIds));
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(rbacService.getAllPermissions());
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        rbacService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
