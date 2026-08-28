package com.worktrack.scheduler;

import com.worktrack.entity.SyncLog;
import com.worktrack.repository.SyncLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemMaintenanceScheduler {

    private final SyncLogRepository syncLogRepository;

    @Scheduled(cron = "0 0 3 * * SUN") // Runs every Sunday at 03:00 AM
    @Transactional
    public int processSystemMaintenance() {
        log.info("Running SystemMaintenanceScheduler for housekeeping...");

        LocalDateTime threshold = LocalDateTime.now().minusDays(90);
        List<SyncLog> oldLogs = syncLogRepository.findAll().stream()
                .filter(log -> log.getCreatedAt() != null && log.getCreatedAt().isBefore(threshold))
                .toList();

        if (!oldLogs.isEmpty()) {
            syncLogRepository.deleteAll(oldLogs);
        }

        log.info("SystemMaintenanceScheduler completed. Purged {} expired sync logs.", oldLogs.size());
        return oldLogs.size();
    }
}
