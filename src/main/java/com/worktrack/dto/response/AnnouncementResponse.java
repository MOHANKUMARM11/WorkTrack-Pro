package com.worktrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {

    private Long id;

    private String title;

    private String content;

    private Long companyId;

    private String companyName;

    private Long createdByUserId;

    private String createdByUserName;

    private String targetRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
