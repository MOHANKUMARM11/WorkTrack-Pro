package com.worktrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "batch_id", nullable = false)
    private String batchId;

    @Column(name = "processed_count", nullable = false)
    private Integer processedCount;

    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @Column(name = "failure_count", nullable = false)
    private Integer failureCount;

    @Column(nullable = false)
    @Builder.Default
    private String status = "COMPLETED";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "COMPLETED";
        }
    }
}
