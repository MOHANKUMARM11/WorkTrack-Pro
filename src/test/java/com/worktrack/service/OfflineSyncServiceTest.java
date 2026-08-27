package com.worktrack.service;

import com.worktrack.dto.request.SyncBatchRequest;
import com.worktrack.dto.request.SyncItemRequest;
import com.worktrack.dto.response.SyncBatchResponse;
import com.worktrack.entity.SyncLog;
import com.worktrack.repository.SyncLogRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.serviceImpl.OfflineSyncServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfflineSyncServiceTest {

    @Mock
    private SyncLogRepository syncLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OfflineSyncServiceImpl offlineSyncService;

    private SyncBatchRequest batchRequest;

    @BeforeEach
    void setUp() {
        SyncItemRequest checkinItem = SyncItemRequest.builder()
                .clientItemId("item-001")
                .actionType("ATTENDANCE_CHECKIN")
                .payload(Map.of("latitude", 12.9716, "longitude", 77.5946))
                .build();

        SyncItemRequest taskItem = SyncItemRequest.builder()
                .clientItemId("item-002")
                .actionType("TASK_STATUS_UPDATE")
                .entityId(10L)
                .payload(Map.of("status", "COMPLETED"))
                .build();

        batchRequest = SyncBatchRequest.builder()
                .batchId("batch-12345")
                .userId(1L)
                .items(List.of(checkinItem, taskItem))
                .build();
    }

    @Test
    @DisplayName("Should process batch sync successfully and persist sync log")
    void processBatchSync_Success() {
        when(syncLogRepository.save(any(SyncLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SyncBatchResponse response = offlineSyncService.processBatchSync(batchRequest);

        assertThat(response).isNotNull();
        assertThat(response.getBatchId()).isEqualTo("batch-12345");
        assertThat(response.getProcessedCount()).isEqualTo(2);
        assertThat(response.getSuccessCount()).isEqualTo(2);
        assertThat(response.getFailureCount()).isEqualTo(0);
        assertThat(response.getItemResults()).hasSize(2);
    }
}
