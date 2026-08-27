package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.SyncBatchRequest;
import com.worktrack.dto.request.SyncItemRequest;
import com.worktrack.dto.response.SyncBatchResponse;
import com.worktrack.dto.response.SyncItemResult;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.OfflineSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SyncControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private OfflineSyncService offlineSyncService;

    @InjectMocks
    private SyncController syncController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(syncController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/sync/batch should process batch offline sync and return 200 OK")
    void processBatchSync_ReturnsOk() throws Exception {
        SyncItemRequest item = SyncItemRequest.builder()
                .clientItemId("item-001")
                .actionType("ATTENDANCE_CHECKIN")
                .build();

        SyncBatchRequest request = SyncBatchRequest.builder()
                .batchId("batch-12345")
                .userId(1L)
                .items(List.of(item))
                .build();

        SyncItemResult itemResult = SyncItemResult.builder()
                .clientItemId("item-001")
                .actionType("ATTENDANCE_CHECKIN")
                .status("SUCCESS")
                .message("Action replayed successfully")
                .build();

        SyncBatchResponse response = SyncBatchResponse.builder()
                .batchId("batch-12345")
                .processedCount(1)
                .successCount(1)
                .failureCount(0)
                .status("COMPLETED")
                .itemResults(List.of(itemResult))
                .build();

        when(offlineSyncService.processBatchSync(any(SyncBatchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/sync/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batchId").value("batch-12345"))
                .andExpect(jsonPath("$.successCount").value(1));
    }
}
