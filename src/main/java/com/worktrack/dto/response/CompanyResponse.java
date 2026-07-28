package com.worktrack.dto.response;

import com.worktrack.constants.CompanyStatus;
import com.worktrack.constants.SubscriptionPlan;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyResponse {

    private Long id;

    private String name;

    private String registrationNumber;

    private String industry;

    private SubscriptionPlan subscriptionPlan;

    private CompanyStatus status;

    private String timezone;

}