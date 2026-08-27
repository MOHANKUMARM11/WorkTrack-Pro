package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncBatchResponse {

    private String batchId;

    private Integer processedCount;

    private Integer successCount;

    private Integer failureCount;

    private String status;

    private List<SyncItemResult> itemResults;

    private LocalDateTime processedAt;
}
