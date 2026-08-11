package com.worktrack.service;

import com.worktrack.constants.ResourceStatus;
import com.worktrack.dto.request.ResourceRequest;
import com.worktrack.dto.response.ResourceResponse;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse getResourceById(Long id);

    List<ResourceResponse> getAllResources();

    ResourceResponse updateResource(Long id, ResourceRequest request);

    ResourceResponse updateResourceStatus(Long id, ResourceStatus status);

    void deleteResource(Long id);
}