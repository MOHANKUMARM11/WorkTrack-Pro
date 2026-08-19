package com.worktrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ManualCheckInApprovalRequest(

        @NotBlank(message = "Approval note is required")
        String approvalNote,

        String photoUrl
) {
}