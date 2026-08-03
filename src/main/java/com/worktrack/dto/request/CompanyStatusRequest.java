package com.worktrack.dto.request;

import com.worktrack.constants.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyStatusRequest {

    @NotNull(message = "Company status is required")
    private CompanyStatus status;

}