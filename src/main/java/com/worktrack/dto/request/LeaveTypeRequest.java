package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Days allowed per year is required")
    @Positive(message = "Days allowed must be positive")
    private Integer daysAllowedPerYear;

    private Boolean carryForwardAllowed = false;

    private Boolean isPaid = true;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}
