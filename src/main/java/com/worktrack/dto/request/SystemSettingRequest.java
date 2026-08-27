package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingRequest {

    @NotBlank(message = "Setting key is required")
    private String key;

    @NotBlank(message = "Setting value is required")
    private String value;

    @Builder.Default
    private String category = "GENERAL";

    private String description;

    private Long companyId;
}
