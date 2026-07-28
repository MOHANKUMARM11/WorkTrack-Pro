package com.worktrack.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollRequest {

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @NotNull
    private Integer year;

    @NotNull
    private BigDecimal basicSalary;

    private BigDecimal allowance = BigDecimal.ZERO;

    private BigDecimal bonus = BigDecimal.ZERO;

    private BigDecimal deduction = BigDecimal.ZERO;

    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull
    private Long employeeId;

    @NotNull
    private Long companyId;
}