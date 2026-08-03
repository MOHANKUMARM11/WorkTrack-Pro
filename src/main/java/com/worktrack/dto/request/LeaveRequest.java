package com.worktrack.dto.request;

import com.worktrack.constants.LeaveType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequest {

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;

    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 255,
            message = "Reason must be between 5 and 255 characters")
    private String reason;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}