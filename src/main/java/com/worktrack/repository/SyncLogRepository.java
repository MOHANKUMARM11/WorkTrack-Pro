package com.worktrack.repository;

import com.worktrack.entity.SyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyncLogRepository extends JpaRepository<SyncLog, Long> {

    List<SyncLog> findByUserId(Long userId);

    Optional<SyncLog> findByBatchId(String batchId);
}
