package com.worktrack.dto.request;

import com.worktrack.constants.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String registrationNumber;

    @NotBlank
    private String industry;

    @NotNull
    private SubscriptionPlan subscriptionPlan;

    @NotBlank
    private String timezone;

}