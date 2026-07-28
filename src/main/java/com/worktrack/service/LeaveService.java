package com.worktrack.service;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.dto.request.LeaveRequest;
import com.worktrack.dto.response.LeaveResponse;

import java.util.List;

public interface LeaveService {

    LeaveResponse createLeave(LeaveRequest request);

    LeaveResponse getLeaveById(Long id);

    List<LeaveResponse> getAllLeaves();

    LeaveResponse updateLeave(Long id, LeaveRequest request);

    LeaveResponse updateLeaveStatus(Long id, LeaveStatus status);

    void deleteLeave(Long id);
}