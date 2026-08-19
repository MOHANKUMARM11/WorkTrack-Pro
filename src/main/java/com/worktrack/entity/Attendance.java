package com.worktrack.entity;

import com.worktrack.constants.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "attendance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_employee_date",
                        columnNames = {"employee_id", "attendance_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Column(name = "working_hours")
    private Double workingHours;

    @Column(name = "overtime_minutes")
    @Builder.Default
    private Integer overtimeMinutes = 0;

    @Column(name = "is_late", nullable = false)
    @Builder.Default
    private Boolean late = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (status == null) {
            status = AttendanceStatus.PRESENT;
        }

        if (overtimeMinutes == null) {
            overtimeMinutes = 0;
        }

        if (late == null) {
            late = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();

        if (overtimeMinutes == null) {
            overtimeMinutes = 0;
        }

        if (late == null) {
            late = false;
        }
    }
}