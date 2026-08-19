package com.worktrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "geofences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_geofence_office",
                        columnNames = "office_location_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Geofence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "office_location_id",
            nullable = false,
            unique = true
    )
    private OfficeLocation officeLocation;

    @Column(
            name = "radius_m",
            nullable = false
    )
    private Double radiusM;

    /*
     * Stored as comma-separated values.
     *
     * Example:
     * "BEACON-001,BEACON-002"
     */
    @Column(
            name = "beacon_ids",
            columnDefinition = "TEXT"
    )
    private String beaconIds;

    /*
     * Stored as comma-separated BSSIDs.
     *
     * Example:
     * "AA:BB:CC:DD:EE:FF,11:22:33:44:55:66"
     */
    @Column(
            name = "wifi_bssids",
            columnDefinition = "TEXT"
    )
    private String wifiBssids;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}