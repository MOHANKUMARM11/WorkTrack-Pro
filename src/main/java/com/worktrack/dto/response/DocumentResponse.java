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
public class DocumentResponse {

    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String filePath;

    private String category;

    private Long uploaderId;

    private Long companyId;

    private LocalDateTime createdAt;
}
