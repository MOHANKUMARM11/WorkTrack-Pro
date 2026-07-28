package com.worktrack.dto.response;

import com.worktrack.constants.EmployeeStatus;
import com.worktrack.constants.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private UserRole role;

    private EmployeeStatus status;

    private Long companyId;

    private String companyName;
}