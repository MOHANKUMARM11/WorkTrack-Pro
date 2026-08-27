package com.worktrack.service;

import com.worktrack.dto.request.LeaveTypeRequest;
import com.worktrack.dto.response.LeaveTypeResponse;

import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponse createLeaveType(LeaveTypeRequest request);

    LeaveTypeResponse getLeaveTypeById(Long id);

    List<LeaveTypeResponse> getLeaveTypesByCompanyId(Long companyId);

    LeaveTypeResponse updateLeaveType(Long id, LeaveTypeRequest request);

    void deleteLeaveType(Long id);
}
