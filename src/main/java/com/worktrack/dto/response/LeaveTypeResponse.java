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
public class LeaveTypeResponse {

    private Long id;

    private String name;

    private String code;

    private Integer daysAllowedPerYear;

    private Boolean carryForwardAllowed;

    private Boolean isPaid;

    private Long companyId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
