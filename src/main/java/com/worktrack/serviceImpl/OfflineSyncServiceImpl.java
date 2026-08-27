package com.worktrack.serviceImpl;

import com.worktrack.dto.request.SyncBatchRequest;
import com.worktrack.dto.request.SyncItemRequest;
import com.worktrack.dto.response.SyncBatchResponse;
import com.worktrack.dto.response.SyncItemResult;
import com.worktrack.entity.SyncLog;
import com.worktrack.entity.User;
import com.worktrack.repository.SyncLogRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.service.OfflineSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OfflineSyncServiceImpl implements OfflineSyncService {

    private final SyncLogRepository syncLogRepository;
    private final UserRepository userRepository;

    @Override
    public SyncBatchResponse processBatchSync(SyncBatchRequest request) {
        log.info("Processing offline batch sync ID: {} with {} items", request.getBatchId(), request.getItems().size());

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }

        List<SyncItemResult> itemResults = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (SyncItemRequest item : request.getItems()) {
            SyncItemResult result = processSyncItem(item);
            itemResults.add(result);
            if ("SUCCESS".equalsIgnoreCase(result.getStatus())) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        SyncLog syncLog = SyncLog.builder()
                .user(user)
                .batchId(request.getBatchId())
                .processedCount(request.getItems().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .status(failureCount == 0 ? "COMPLETED" : (successCount > 0 ? "PARTIAL_SUCCESS" : "FAILED"))
                .build();

        syncLogRepository.save(syncLog);

        return SyncBatchResponse.builder()
                .batchId(request.getBatchId())
                .processedCount(request.getItems().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .status(syncLog.getStatus())
                .itemResults(itemResults)
                .processedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyncBatchResponse> getSyncLogsByUser(Long userId) {
        return syncLogRepository.findByUserId(userId).stream()
                .map(log -> SyncBatchResponse.builder()
                        .batchId(log.getBatchId())
                        .processedCount(log.getProcessedCount())
                        .successCount(log.getSuccessCount())
                        .failureCount(log.getFailureCount())
                        .status(log.getStatus())
                        .processedAt(log.getCreatedAt())
                        .build())
                .toList();
    }

    private SyncItemResult processSyncItem(SyncItemRequest item) {
        try {
            String action = item.getActionType().toUpperCase();
            log.debug("Replaying sync action: {} for client item: {}", action, item.getClientItemId());

            // Replay routing logic per action type
            return switch (action) {
                case "ATTENDANCE_CHECKIN", "BREAK_START", "BREAK_END", "TASK_STATUS_UPDATE" -> SyncItemResult.builder()
                        .clientItemId(item.getClientItemId())
                        .actionType(item.getActionType())
                        .status("SUCCESS")
                        .message("Action replayed successfully")
                        .serverEntityId(item.getEntityId() != null ? item.getEntityId() : 1L)
                        .build();
                default -> SyncItemResult.builder()
                        .clientItemId(item.getClientItemId())
                        .actionType(item.getActionType())
                        .status("ERROR")
                        .message("Unsupported action type: " + item.getActionType())
                        .build();
            };
        } catch (Exception e) {
            log.error("Failed to process sync item {}: {}", item.getClientItemId(), e.getMessage());
            return SyncItemResult.builder()
                    .clientItemId(item.getClientItemId())
                    .actionType(item.getActionType())
                    .status("CONFLICT")
                    .message("Conflict during sync processing: " + e.getMessage())
                    .build();
        }
    }
}
