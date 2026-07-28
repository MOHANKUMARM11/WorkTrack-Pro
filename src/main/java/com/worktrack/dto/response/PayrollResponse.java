package com.worktrack.dto.response;

import com.worktrack.constants.PayrollStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PayrollResponse {

    private Long id;

    private Integer month;

    private Integer year;

    private BigDecimal basicSalary;

    private BigDecimal allowance;

    private BigDecimal bonus;

    private BigDecimal deduction;

    private BigDecimal tax;

    private BigDecimal netSalary;

    private PayrollStatus status;

    private Long employeeId;

    private String employeeName;

    private Long companyId;

    private String companyName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}