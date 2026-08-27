package com.worktrack.service;

import com.worktrack.dto.request.ShiftAssignmentRequest;
import com.worktrack.dto.request.ShiftRequest;
import com.worktrack.dto.response.ShiftAssignmentResponse;
import com.worktrack.dto.response.ShiftResponse;

import java.time.LocalDate;
import java.util.List;

public interface ShiftService {

    ShiftResponse createShift(ShiftRequest request);

    ShiftResponse getShiftById(Long id);

    List<ShiftResponse> getShiftsByCompanyId(Long companyId);

    ShiftAssignmentResponse assignShiftToEmployee(ShiftAssignmentRequest request);

    List<ShiftAssignmentResponse> getAssignmentsByEmployee(Long employeeId);

    ShiftAssignmentResponse getActiveAssignmentForEmployeeOnDate(Long employeeId, LocalDate date);

    void deleteShift(Long id);
}
