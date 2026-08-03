package com.worktrack.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollRequest {

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year must not exceed 2100")
    private Integer year;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Basic salary must be greater than 0")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", message = "Allowance cannot be negative")
    private BigDecimal allowance = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Bonus cannot be negative")
    private BigDecimal bonus = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Deduction cannot be negative")
    private BigDecimal deduction = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Tax cannot be negative")
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}