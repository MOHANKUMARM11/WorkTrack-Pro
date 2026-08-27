package com.worktrack.serviceImpl;

import com.worktrack.constants.AttendanceEventType;
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
import com.worktrack.entity.Attendance;
import com.worktrack.entity.AttendanceLog;
import com.worktrack.entity.Break;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Geofence;
import com.worktrack.entity.GpsLocation;
import com.worktrack.entity.OfficeLocation;
import com.worktrack.exception.custom.AttendanceNotFoundException;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.DuplicateAttendanceException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.mapper.AttendanceMapper;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.notification.event.AttendanceLateEvent;
import com.worktrack.repository.AttendanceLogRepository;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.BreakRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.GeofenceRepository;
import com.worktrack.repository.GpsLocationRepository;
import com.worktrack.repository.OfficeLocationRepository;
import com.worktrack.service.AttendanceService;
import com.worktrack.service.EmployeeDeviceService;
import com.worktrack.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private static final double DEFAULT_GPS_ACCURACY_METERS = 100.0;
    private static final int DEFAULT_LATE_GRACE_MINUTES = 15;
    private static final int DEFAULT_STANDARD_SHIFT_MINUTES = 480;

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final NotificationEventProducer notificationEventProducer;

    private final AttendanceLogRepository attendanceLogRepository;
    private final GpsLocationRepository gpsLocationRepository;
    private final GeofenceRepository geofenceRepository;
    private final OfficeLocationRepository officeLocationRepository;
    private final BreakRepository breakRepository;
    private final EmployeeDeviceService employeeDeviceService;

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

        updateLateAndOvertime(attendance);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        publishLateEventIfRequired(savedAttendance);

        return AttendanceMapper.toResponse(savedAttendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long id) {

        Attendance attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        return AttendanceMapper.toResponse(attendance);
    }

    @Override
    @Transactional(readOnly = true)
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

        updateLateAndOvertime(attendance);

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

        updateLateAndOvertime(attendance);

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

    @Override
    public AttendanceCheckInResponse checkIn(
            Long employeeId,
            AttendanceCheckInRequest request) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Employee not found"));

        employeeDeviceService.verifyDevice(
                employeeId,
                request.deviceId(),
                request.deviceSecret());

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmployeeIdAndAttendanceDate(
                employeeId,
                today)) {

            throw new DuplicateAttendanceException(
                    "Attendance already exists for this employee today");
        }

        LocationValidationResult validation =
                validateLocation(employee, request);

        Attendance attendance =
                Attendance.builder()
                        .attendanceDate(today)
                        .checkIn(LocalTime.now())
                        .status(AttendanceStatus.PRESENT)
                        .employee(employee)
                        .company(employee.getCompany())
                        .workingHours(null)
                        .overtimeMinutes(0)
                        .late(false)
                        .build();

        updateLateAndOvertime(attendance);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        AttendanceLog log =
                AttendanceLog.builder()
                        .attendance(savedAttendance)
                        .eventType(AttendanceEventType.CHECK_IN)
                        .latitude(request.latitude())
                        .longitude(request.longitude())
                        .accuracyM(request.accuracyM())
                        .source(validation.source())
                        .deviceSignature(request.deviceId())
                        .beaconId(request.beaconId())
                        .wifiBssid(request.wifiBssid())
                        .manualNote(request.manualNote())
                        .photoUrl(request.photoUrl())
                        .build();

        AttendanceLog savedLog =
                attendanceLogRepository.save(log);

        if (request.latitude() != null
                && request.longitude() != null) {

            GpsLocation gpsLocation =
                    GpsLocation.builder()
                            .attendanceLog(savedLog)
                            .latitude(request.latitude())
                            .longitude(request.longitude())
                            .build();

            gpsLocationRepository.save(gpsLocation);
        }

        publishLateEventIfRequired(savedAttendance);

        return toCheckInResponse(
                savedAttendance,
                validation);
    }

    @Override
    public AttendanceCheckInResponse checkOut(
            Long employeeId,
            AttendanceCheckOutRequest request) {

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Employee not found"));

        Attendance attendance =
                attendanceRepository
                        .findByEmployeeIdAndAttendanceDate(
                                employeeId,
                                LocalDate.now())
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Today's attendance not found"));

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException(
                    "Attendance has already been checked out");
        }

        employeeDeviceService.verifyDevice(
                employeeId,
                request.deviceId(),
                request.deviceSecret());

        LocationValidationResult validation =
                validateLocation(employee, request);

        LocalTime checkOut = LocalTime.now();

        attendance.setCheckOut(checkOut);

        calculateWorkingHours(attendance);
        updateLateAndOvertime(attendance);

        Attendance savedAttendance =
                attendanceRepository.save(attendance);

        AttendanceLog log =
                AttendanceLog.builder()
                        .attendance(savedAttendance)
                        .eventType(AttendanceEventType.CHECK_OUT)
                        .latitude(request.latitude())
                        .longitude(request.longitude())
                        .accuracyM(request.accuracyM())
                        .source(validation.source())
                        .deviceSignature(request.deviceId())
                        .beaconId(request.beaconId())
                        .wifiBssid(request.wifiBssid())
                        .build();

        AttendanceLog savedLog =
                attendanceLogRepository.save(log);

        if (request.latitude() != null
                && request.longitude() != null) {

            GpsLocation gpsLocation =
                    GpsLocation.builder()
                            .attendanceLog(savedLog)
                            .latitude(request.latitude())
                            .longitude(request.longitude())
                            .build();

            gpsLocationRepository.save(gpsLocation);
        }

        return toCheckInResponse(
                savedAttendance,
                validation);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceTodayResponse getTodayAttendance(
            Long employeeId) {

        Attendance attendance =
                attendanceRepository
                        .findByEmployeeIdAndAttendanceDate(
                                employeeId,
                                LocalDate.now())
                        .orElse(null);

        if (attendance == null) {
            return new AttendanceTodayResponse(
                    null,
                    employeeId,
                    LocalDate.now(),
                    null,
                    null,
                    null,
                    null,
                    false,
                    false
            );
        }

        return new AttendanceTodayResponse(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getAttendanceDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getWorkingHours(),
                attendance.getStatus(),
                attendance.getCheckIn() != null,
                attendance.getCheckOut() != null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceHistoryResponse> getAttendanceHistory(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate,
            AttendanceStatus status) {

        List<Attendance> records;

        if (startDate != null && endDate != null) {

            records =
                    attendanceRepository
                            .findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                                    employeeId,
                                    startDate,
                                    endDate);

        } else if (status != null) {

            records =
                    attendanceRepository
                            .findByEmployeeIdAndStatusOrderByAttendanceDateDesc(
                                    employeeId,
                                    status);

        } else {

            records =
                    attendanceRepository
                            .findByEmployeeIdOrderByAttendanceDateDesc(
                                    employeeId);
        }

        if (status != null
                && startDate != null
                && endDate != null) {

            records =
                    records.stream()
                            .filter(a -> a.getStatus() == status)
                            .toList();
        }

        return records.stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceLogResponse> getAttendanceLogs(
            Long attendanceId) {

        if (!attendanceRepository.existsById(attendanceId)) {
            throw new AttendanceNotFoundException(
                    "Attendance not found");
        }

        return attendanceLogRepository
                .findByAttendanceIdOrderByCreatedAtAsc(attendanceId)
                .stream()
                .map(this::toLogResponse)
                .toList();
    }

    @Override
    public BreakResponse startBreak(
            Long employeeId,
            BreakRequest request) {

        Attendance attendance =
                attendanceRepository
                        .findById(request.attendanceId())
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        if (!attendance.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException(
                    "Attendance does not belong to this employee");
        }

        if (attendance.getCheckIn() == null) {
            throw new IllegalStateException(
                    "Employee must check in before starting a break");
        }

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException(
                    "Cannot start a break after check-out");
        }

        if (breakRepository
                .findFirstByAttendanceIdAndEndAtIsNullOrderByStartAtDesc(
                        attendance.getId())
                .isPresent()) {

            throw new IllegalStateException(
                    "An active break already exists");
        }

        Break breakRecord =
                Break.builder()
                        .attendance(attendance)
                        .startAt(LocalDateTime.now())
                        .build();

        Break saved =
                breakRepository.save(breakRecord);

        AttendanceLog log =
                AttendanceLog.builder()
                        .attendance(attendance)
                        .eventType(AttendanceEventType.BREAK_START)
                        .source("MANUAL")
                        .manualNote("Break started")
                        .build();

        attendanceLogRepository.save(log);

        return toBreakResponse(saved);
    }

    @Override
    public BreakResponse endBreak(
            Long employeeId) {

        Attendance attendance =
                attendanceRepository
                        .findByEmployeeIdAndAttendanceDate(
                                employeeId,
                                LocalDate.now())
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Today's attendance not found"));

        Break breakRecord =
                breakRepository
                        .findFirstByAttendanceIdAndEndAtIsNullOrderByStartAtDesc(
                                attendance.getId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No active break found"));

        LocalDateTime endAt = LocalDateTime.now();

        breakRecord.setEndAt(endAt);

        long minutes =
                Duration.between(
                                breakRecord.getStartAt(),
                                endAt)
                        .toMinutes();

        breakRecord.setDurationMinutes(
                (int) Math.max(minutes, 0));

        Break saved =
                breakRepository.save(breakRecord);

        AttendanceLog log =
                AttendanceLog.builder()
                        .attendance(attendance)
                        .eventType(AttendanceEventType.BREAK_END)
                        .source("MANUAL")
                        .manualNote("Break ended")
                        .build();

        attendanceLogRepository.save(log);

        return toBreakResponse(saved);
    }

    @Override
    public AttendanceResponse requestCorrection(
            Long attendanceId,
            Long employeeId,
            AttendanceCorrectionRequest request) {

        Attendance attendance =
                attendanceRepository.findById(attendanceId)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        if (!attendance.getEmployee().getId().equals(employeeId)) {
            throw new IllegalArgumentException(
                    "Attendance does not belong to this employee");
        }

        if (request.checkIn() != null) {
            attendance.setCheckIn(request.checkIn());
        }

        if (request.checkOut() != null) {
            attendance.setCheckOut(request.checkOut());
        }

        calculateWorkingHours(attendance);
        updateLateAndOvertime(attendance);

        Attendance saved =
                attendanceRepository.save(attendance);

        AttendanceLog log =
                AttendanceLog.builder()
                        .attendance(saved)
                        .eventType(AttendanceEventType.CORRECTION)
                        .source("MANUAL")
                        .manualNote(request.reason())
                        .deviceSignature(request.deviceSignature())
                        .build();

        attendanceLogRepository.save(log);

        publishLateEventIfRequired(saved);

        return AttendanceMapper.toResponse(saved);
    }

    @Override
    public AttendanceResponse approveManualCheckIn(
            Long attendanceId,
            Long managerId,
            ManualCheckInApprovalRequest request) {

        Employee manager =
                employeeRepository.findById(managerId)
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Manager not found"));

        Attendance attendance =
                attendanceRepository.findById(attendanceId)
                        .orElseThrow(() ->
                                new AttendanceNotFoundException(
                                        "Attendance not found"));

        if (request.approvalNote() == null
                || request.approvalNote().isBlank()) {

            throw new IllegalArgumentException(
                    "Approval note is required");
        }

        if (attendance.getCheckIn() == null) {
            attendance.setCheckIn(LocalTime.now());
        }

        updateLateAndOvertime(attendance);

        Attendance saved =
                attendanceRepository.save(attendance);

        AttendanceLog log =
                AttendanceLog.builder()
                        .attendance(saved)
                        .eventType(AttendanceEventType.CHECK_IN)
                        .source("MANUAL")
                        .manualNote(
                                "Approved by manager "
                                        + manager.getId()
                                        + ": "
                                        + request.approvalNote())
                        .photoUrl(request.photoUrl())
                        .build();

        attendanceLogRepository.save(log);

        publishLateEventIfRequired(saved);

        return AttendanceMapper.toResponse(saved);
    }

    private LocationValidationResult validateLocation(
            Employee employee,
            AttendanceCheckInRequest request) {

        if (isValidGps(
                request.latitude(),
                request.longitude(),
                request.accuracyM())) {

            return validateGps(
                    employee,
                    request.latitude(),
                    request.longitude(),
                    request.accuracyM(),
                    request.beaconId(),
                    request.wifiBssid());
        }

        return validateFallback(
                employee,
                request.beaconId(),
                request.wifiBssid(),
                request.manualNote());
    }

    private LocationValidationResult validateLocation(
            Employee employee,
            AttendanceCheckOutRequest request) {

        if (isValidGps(
                request.latitude(),
                request.longitude(),
                request.accuracyM())) {

            return validateGps(
                    employee,
                    request.latitude(),
                    request.longitude(),
                    request.accuracyM(),
                    request.beaconId(),
                    request.wifiBssid());
        }

        return validateFallback(
                employee,
                request.beaconId(),
                request.wifiBssid(),
                null);
    }

    private LocationValidationResult validateGps(
            Employee employee,
            Double latitude,
            Double longitude,
            Double accuracyM,
            String beaconId,
            String wifiBssid) {

        OfficeLocation office =
                employee.getOfficeLocation();

        if (office == null) {
            throw new IllegalStateException(
                    "Employee does not have an office location assigned");
        }

        if (accuracyM > DEFAULT_GPS_ACCURACY_METERS) {

            return validateFallback(
                    employee,
                    beaconId,
                    wifiBssid,
                    null);
        }

        double distance =
                GeoUtils.distanceMeters(
                        latitude,
                        longitude,
                        office.getLatitude(),
                        office.getLongitude());

        double radius =
                office.getGeofenceRadiusM();

        Geofence geofence =
                geofenceRepository
                        .findByOfficeLocationId(office.getId())
                        .orElse(null);

        if (geofence != null) {
            radius = geofence.getRadiusM();
        }

        if (distance <= radius) {

            return new LocationValidationResult(
                    "GPS",
                    distance,
                    true);
        }

        return validateFallback(
                employee,
                null,
                null,
                null);
    }

    private LocationValidationResult validateFallback(
            Employee employee,
            String beaconId,
            String wifiBssid,
            String manualNote) {

        OfficeLocation office =
                employee.getOfficeLocation();

        if (office == null) {
            throw new IllegalStateException(
                    "Employee does not have an office location assigned");
        }

        Geofence geofence =
                geofenceRepository
                        .findByOfficeLocationId(office.getId())
                        .orElse(null);

        if (geofence != null) {

            if (matchesBeacon(
                    geofence.getBeaconIds(),
                    beaconId)) {

                return new LocationValidationResult(
                        "BLE",
                        null,
                        true);
            }

            if (matchesWifi(
                    geofence.getWifiBssids(),
                    wifiBssid)) {

                return new LocationValidationResult(
                        "WIFI",
                        null,
                        true);
            }
        }

        if (manualNote != null
                && !manualNote.isBlank()) {

            return new LocationValidationResult(
                    "MANUAL",
                    null,
                    false);
        }

        throw new IllegalArgumentException(
                "Unable to verify attendance location. GPS, BLE, Wi-Fi or manual fallback is required");
    }

    private boolean matchesBeacon(
            String configuredBeacons,
            String beaconId) {

        if (configuredBeacons == null
                || configuredBeacons.isBlank()
                || beaconId == null
                || beaconId.isBlank()) {

            return false;
        }

        return List.of(
                        configuredBeacons
                                .split(","))
                .stream()
                .map(String::trim)
                .anyMatch(
                        configured ->
                                configured.equalsIgnoreCase(
                                        beaconId.trim()));
    }

    private boolean matchesWifi(
            String configuredBssids,
            String wifiBssid) {

        if (configuredBssids == null
                || configuredBssids.isBlank()
                || wifiBssid == null
                || wifiBssid.isBlank()) {

            return false;
        }

        return List.of(
                        configuredBssids
                                .split(","))
                .stream()
                .map(String::trim)
                .anyMatch(
                        configured ->
                                configured.equalsIgnoreCase(
                                        wifiBssid.trim()));
    }

    private boolean isValidGps(
            Double latitude,
            Double longitude,
            Double accuracyM) {

        return latitude != null
                && longitude != null
                && accuracyM != null
                && accuracyM >= 0;
    }

    private void calculateWorkingHours(
            Attendance attendance) {

        if (attendance.getCheckIn() == null
                || attendance.getCheckOut() == null) {

            return;
        }

        long minutes =
                Duration.between(
                                attendance.getCheckIn(),
                                attendance.getCheckOut())
                        .toMinutes();

        if (minutes < 0) {
            throw new IllegalArgumentException(
                    "Check-out time cannot be before check-in time");
        }

        attendance.setWorkingHours(
                minutes / 60.0);

        attendance.setOvertimeMinutes(
                (int) Math.max(
                        minutes - DEFAULT_STANDARD_SHIFT_MINUTES,
                        0));
    }

    private void updateLateAndOvertime(
            Attendance attendance) {

        if (attendance.getCheckIn() != null) {

            LocalTime lateThreshold =
                    LocalTime.of(
                            9,
                            DEFAULT_LATE_GRACE_MINUTES);

            boolean isLate =
                    attendance.getCheckIn()
                            .isAfter(lateThreshold);

            attendance.setLate(isLate);

            if (isLate) {
                attendance.setStatus(AttendanceStatus.LATE);
            } else if (attendance.getStatus() == AttendanceStatus.LATE) {
                attendance.setStatus(AttendanceStatus.PRESENT);
            }
        }

        if (attendance.getCheckIn() != null
                && attendance.getCheckOut() != null) {

            calculateWorkingHours(attendance);
        }
    }

    private void publishLateEventIfRequired(
            Attendance attendance) {

        if (attendance.getStatus()
                == AttendanceStatus.LATE) {

            notificationEventProducer.publishAttendanceLate(
                    new AttendanceLateEvent(
                            attendance.getId(),
                            attendance.getEmployee().getId(),
                            attendance.getAttendanceDate()));
        }
    }

    private AttendanceCheckInResponse toCheckInResponse(
            Attendance attendance,
            LocationValidationResult validation) {

        return new AttendanceCheckInResponse(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getCompany().getId(),
                attendance.getAttendanceDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getWorkingHours(),
                attendance.getStatus(),
                validation.source(),
                validation.distanceMeters(),
                validation.withinGeofence(),
                LocalDateTime.now()
        );
    }

    private AttendanceHistoryResponse toHistoryResponse(
            Attendance attendance) {

        return new AttendanceHistoryResponse(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getCompany().getId(),
                attendance.getAttendanceDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getWorkingHours(),
                attendance.getStatus()
        );
    }

    private AttendanceLogResponse toLogResponse(
            AttendanceLog log) {

        return new AttendanceLogResponse(
                log.getId(),
                log.getAttendance().getId(),
                log.getEventType(),
                log.getLatitude(),
                log.getLongitude(),
                log.getAccuracyM(),
                log.getSource(),
                log.getBeaconId(),
                log.getWifiBssid(),
                log.getManualNote(),
                log.getPhotoUrl(),
                log.getCreatedAt()
        );
    }

    private BreakResponse toBreakResponse(
            Break breakRecord) {

        return new BreakResponse(
                breakRecord.getId(),
                breakRecord.getAttendance().getId(),
                breakRecord.getStartAt(),
                breakRecord.getEndAt(),
                breakRecord.getDurationMinutes()
        );
    }

    private record LocationValidationResult(
            String source,
            Double distanceMeters,
            Boolean withinGeofence) {
    }
}