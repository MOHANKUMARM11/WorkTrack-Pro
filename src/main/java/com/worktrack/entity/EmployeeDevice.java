package com.worktrack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "employee_devices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_device",
                        columnNames = {"employee_id", "device_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    @Column(name = "secret_hash", nullable = false, length = 255)
    private String secretHash;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (active == null) {
            active = true;
        }
    }
}