package com.worktrack.controller;

import com.worktrack.dto.request.SystemSettingRequest;
import com.worktrack.dto.response.SystemSettingResponse;
import com.worktrack.service.SystemSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system-settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SystemSettingResponse> saveOrUpdateSetting(@Valid @RequestBody SystemSettingRequest request) {
        return new ResponseEntity<>(systemSettingService.saveOrUpdateSetting(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SystemSettingResponse>> getAllSettings() {
        return ResponseEntity.ok(systemSettingService.getAllSettings());
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<SystemSettingResponse> getSettingByKey(
            @PathVariable String key,
            @RequestParam(required = false) Long companyId) {
        return ResponseEntity.ok(systemSettingService.getSettingByKeyAndCompany(key, companyId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SystemSettingResponse>> getSettingsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(systemSettingService.getSettingsByCompanyId(companyId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        systemSettingService.deleteSetting(id);
        return ResponseEntity.noContent().build();
    }
}
