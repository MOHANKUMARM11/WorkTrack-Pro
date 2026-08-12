package com.worktrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "notification_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_preferences_user_channel",
                        columnNames = {"user_id", "channel"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String channel;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}