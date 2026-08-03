package com.worktrack.dto.request;

import com.worktrack.constants.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan subscriptionPlan;

    @NotBlank(message = "Timezone is required")
    private String timezone;

}