package com.worktrack.dto.request;

import com.worktrack.constants.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyStatusRequest {

    @NotNull
    private CompanyStatus status;
}