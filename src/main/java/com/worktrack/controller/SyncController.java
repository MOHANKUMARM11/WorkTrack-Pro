package com.worktrack.controller;

import com.worktrack.dto.request.SyncBatchRequest;
import com.worktrack.dto.response.SyncBatchResponse;
import com.worktrack.service.OfflineSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final OfflineSyncService offlineSyncService;

    @PostMapping("/batch")
    public ResponseEntity<SyncBatchResponse> processBatchSync(@Valid @RequestBody SyncBatchRequest request) {
        return ResponseEntity.ok(offlineSyncService.processBatchSync(request));
    }

    @GetMapping("/logs/user/{userId}")
    public ResponseEntity<List<SyncBatchResponse>> getSyncLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(offlineSyncService.getSyncLogsByUser(userId));
    }
}
