package com.worktrack.dto.response;

import com.worktrack.constants.ResourceStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceResponse {

    private Long id;

    private String name;

    private String description;

    private String type;

    private Integer quantity;

    private ResourceStatus status;

    private Long companyId;

    private Long employeeId;
}