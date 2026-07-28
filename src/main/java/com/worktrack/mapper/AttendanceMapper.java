package com.worktrack.mapper;

import com.worktrack.dto.request.AttendanceRequest;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.entity.Attendance;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;

import java.time.Duration;

public class AttendanceMapper {

    private AttendanceMapper() {
    }

    public static Attendance toEntity(
            AttendanceRequest request,
            Employee employee,
            Company company) {

        Double workingHours = null;

        if (request.getCheckIn() != null && request.getCheckOut() != null) {
            workingHours = Duration.between(
                    request.getCheckIn(),
                    request.getCheckOut()
            ).toMinutes() / 60.0;
        }

        return Attendance.builder()
                .attendanceDate(request.getAttendanceDate())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .workingHours(workingHours)
                .status(request.getStatus())
                .employee(employee)
                .company(company)
                .build();
    }

    public static AttendanceResponse toResponse(Attendance attendance) {

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .attendanceDate(attendance.getAttendanceDate())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .workingHours(attendance.getWorkingHours())
                .status(attendance.getStatus())
                .employeeId(attendance.getEmployee().getId())
                .employeeName(
                        attendance.getEmployee().getFirstName() + " "
                                + attendance.getEmployee().getLastName())
                .companyId(attendance.getCompany().getId())
                .companyName(attendance.getCompany().getName())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }
}