package com.worktrack.dto.request;

import com.worktrack.constants.EmployeeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeStatusRequest {

    @NotNull(message = "Employee status is required")
    private EmployeeStatus status;

}