package com.worktrack.service;

import com.worktrack.dto.request.SyncBatchRequest;
import com.worktrack.dto.response.SyncBatchResponse;

import java.util.List;

public interface OfflineSyncService {

    SyncBatchResponse processBatchSync(SyncBatchRequest request);

    List<SyncBatchResponse> getSyncLogsByUser(Long userId);
}
