package com.worktrack.service;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceCheckInRequest;
import com.worktrack.dto.request.AttendanceCheckOutRequest;
import com.worktrack.dto.request.AttendanceCorrectionRequest;
import com.worktrack.dto.request.AttendanceRequest;
import com.worktrack.dto.request.BreakRequest;
import com.worktrack.dto.request.ManualCheckInApprovalRequest;
import com.worktrack.dto.response.AttendanceCheckInResponse;
import com.worktrack.dto.response.AttendanceHistoryResponse;
import com.worktrack.dto.response.AttendanceLogResponse;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.dto.response.AttendanceTodayResponse;
import com.worktrack.dto.response.BreakResponse;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponse createAttendance(AttendanceRequest request);

    AttendanceResponse getAttendanceById(Long id);

    List<AttendanceResponse> getAllAttendance();

    AttendanceResponse updateAttendance(
            Long id,
            AttendanceRequest request
    );

    AttendanceResponse updateAttendanceStatus(
            Long id,
            AttendanceStatus status
    );

    void deleteAttendance(Long id);

    AttendanceCheckInResponse checkIn(
            Long employeeId,
            AttendanceCheckInRequest request
    );

    AttendanceCheckInResponse checkOut(
            Long employeeId,
            AttendanceCheckOutRequest request
    );

    AttendanceTodayResponse getTodayAttendance(
            Long employeeId
    );

    List<AttendanceHistoryResponse> getAttendanceHistory(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate,
            AttendanceStatus status
    );

    List<AttendanceLogResponse> getAttendanceLogs(
            Long attendanceId
    );

    BreakResponse startBreak(
            Long employeeId,
            BreakRequest request
    );

    BreakResponse endBreak(
            Long employeeId
    );

    AttendanceResponse requestCorrection(
            Long attendanceId,
            Long employeeId,
            AttendanceCorrectionRequest request
    );

    AttendanceResponse approveManualCheckIn(
            Long attendanceId,
            Long managerId,
            ManualCheckInApprovalRequest request
    );
}