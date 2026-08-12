package com.worktrack.serviceImpl;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceRequest;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.entity.Attendance;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.exception.custom.AttendanceNotFoundException;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.DuplicateAttendanceException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.mapper.AttendanceMapper;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.notification.event.AttendanceLateEvent;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final NotificationEventProducer notificationEventProducer;

    @Override
    public AttendanceResponse createAttendance(
            AttendanceRequest request) {

        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(
                request.getEmployeeId(),
                request.getAttendanceDate())) {

            throw new DuplicateAttendanceException(
                    "Attendance already exists for this employee on this date");
        }

        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Employee not found"));

        Company company =
                companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() ->
                                new CompanyNotFoundException(
                                        "Company not found"));

        Attendance attendance =
                AttendanceMapper.toEntity(
                        request,
                        employee,
                        company);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        publishLateEventIfRequired(savedAttendance);

        return AttendanceMapper.toResponse(savedAttendance);
    }

    @Override
    public AttendanceResponse getAttendanceById(Long id) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        return AttendanceMapper.toResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getAllAttendance() {

        return attendanceRepository.findAll()
                .stream()
                .map(AttendanceMapper::toResponse)
                .toList();
    }

    @Override
    public AttendanceResponse updateAttendance(
            Long id,
            AttendanceRequest request) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Employee not found"));

        Company company =
                companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() ->
                                new CompanyNotFoundException(
                                        "Company not found"));

        Attendance updatedAttendance =
                AttendanceMapper.toEntity(
                        request,
                        employee,
                        company);

        attendance.setAttendanceDate(
                updatedAttendance.getAttendanceDate());

        attendance.setCheckIn(
                updatedAttendance.getCheckIn());

        attendance.setCheckOut(
                updatedAttendance.getCheckOut());

        attendance.setWorkingHours(
                updatedAttendance.getWorkingHours());

        attendance.setStatus(
                updatedAttendance.getStatus());

        attendance.setEmployee(employee);
        attendance.setCompany(company);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        publishLateEventIfRequired(savedAttendance);

        return AttendanceMapper.toResponse(savedAttendance);
    }

    @Override
    public AttendanceResponse updateAttendanceStatus(
            Long id,
            AttendanceStatus status) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        attendance.setStatus(status);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        publishLateEventIfRequired(savedAttendance);

        return AttendanceMapper.toResponse(savedAttendance);
    }

    @Override
    public void deleteAttendance(Long id) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        attendanceRepository.delete(attendance);
    }

    private void publishLateEventIfRequired(
            Attendance attendance) {

        if (attendance.getStatus() == AttendanceStatus.LATE) {

            notificationEventProducer.publishAttendanceLate(
                    new AttendanceLateEvent(
                            attendance.getId(),
                            attendance.getEmployee().getId(),
                            attendance.getAttendanceDate()));
        }
    }
}