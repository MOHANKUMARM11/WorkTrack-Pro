package com.worktrack.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncBatchRequest {

    @NotBlank(message = "Batch ID is required")
    private String batchId;

    private Long userId;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<SyncItemRequest> items;
}
