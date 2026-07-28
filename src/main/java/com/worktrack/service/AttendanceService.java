package com.worktrack.service;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceRequest;
import com.worktrack.dto.response.AttendanceResponse;

import java.util.List;

public interface AttendanceService {

    AttendanceResponse createAttendance(AttendanceRequest request);

    AttendanceResponse getAttendanceById(Long id);

    List<AttendanceResponse> getAllAttendance();

    AttendanceResponse updateAttendance(Long id, AttendanceRequest request);

    AttendanceResponse updateAttendanceStatus(Long id, AttendanceStatus status);

    void deleteAttendance(Long id);
}