package com.worktrack.entity;

import com.worktrack.constants.AttendanceEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private AttendanceEventType eventType;

    private Double latitude;

    private Double longitude;

    @Column(name = "accuracy_m")
    private Double accuracyM;

    @Column(nullable = false, length = 20)
    private String source;

    @Column(name = "device_signature", length = 512)
    private String deviceSignature;

    @Column(name = "beacon_id")
    private String beaconId;

    @Column(name = "wifi_bssid")
    private String wifiBssid;

    @Column(name = "manual_note", columnDefinition = "TEXT")
    private String manualNote;

    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}