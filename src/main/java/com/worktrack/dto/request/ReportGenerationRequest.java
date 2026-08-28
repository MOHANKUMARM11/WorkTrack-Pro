package com.worktrack.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private String format = "CSV";
}
