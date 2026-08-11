package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.constants.ResourceStatus;
import com.worktrack.dto.request.ResourceRequest;
import com.worktrack.dto.response.ResourceResponse;
import com.worktrack.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ResourceResponse> createResource(
            @Valid @RequestBody ResourceRequest request) {

        ResourceResponse response =
                resourceService.createResource(request);

        return ApiResponse.<ResourceResponse>builder()
                .success(true)
                .message("Resource created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ResourceResponse> getResourceById(
            @PathVariable Long id) {

        ResourceResponse response =
                resourceService.getResourceById(id);

        return ApiResponse.<ResourceResponse>builder()
                .success(true)
                .message("Resource retrieved successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<ResourceResponse>> getAllResources() {

        List<ResourceResponse> response =
                resourceService.getAllResources();

        return ApiResponse.<List<ResourceResponse>>builder()
                .success(true)
                .message("Resources retrieved successfully")
                .data(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {

        ResourceResponse response =
                resourceService.updateResource(id, request);

        return ApiResponse.<ResourceResponse>builder()
                .success(true)
                .message("Resource updated successfully")
                .data(response)
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ResourceResponse> updateResourceStatus(
            @PathVariable Long id,
            @RequestParam ResourceStatus status) {

        ResourceResponse response =
                resourceService.updateResourceStatus(id, status);

        return ApiResponse.<ResourceResponse>builder()
                .success(true)
                .message("Resource status updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteResource(
            @PathVariable Long id) {

        resourceService.deleteResource(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Resource deleted successfully")
                .build();
    }
}