package com.worktrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gps_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_log_id", nullable = false)
    private AttendanceLog attendanceLog;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}