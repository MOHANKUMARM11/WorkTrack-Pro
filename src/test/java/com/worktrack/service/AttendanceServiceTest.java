package com.worktrack.service;

import com.worktrack.constants.AttendanceEventType;
import com.worktrack.constants.AttendanceStatus;
import com.worktrack.dto.request.AttendanceCheckInRequest;
import com.worktrack.dto.request.AttendanceCheckOutRequest;
import com.worktrack.dto.request.BreakRequest;
import com.worktrack.dto.request.ManualCheckInApprovalRequest;
import com.worktrack.dto.response.AttendanceCheckInResponse;
import com.worktrack.dto.response.AttendanceResponse;
import com.worktrack.dto.response.BreakResponse;
import com.worktrack.entity.Attendance;
import com.worktrack.entity.AttendanceLog;
import com.worktrack.entity.Break;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.OfficeLocation;
import com.worktrack.exception.custom.DuplicateAttendanceException;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.repository.AttendanceLogRepository;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.BreakRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.GeofenceRepository;
import com.worktrack.repository.GpsLocationRepository;
import com.worktrack.repository.OfficeLocationRepository;
import com.worktrack.serviceImpl.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private NotificationEventProducer notificationEventProducer;
    @Mock
    private AttendanceLogRepository attendanceLogRepository;
    @Mock
    private GpsLocationRepository gpsLocationRepository;
    @Mock
    private GeofenceRepository geofenceRepository;
    @Mock
    private OfficeLocationRepository officeLocationRepository;
    @Mock
    private BreakRepository breakRepository;
    @Mock
    private EmployeeDeviceService employeeDeviceService;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Company sampleCompany;
    private OfficeLocation sampleOffice;
    private Employee sampleEmployee;
    private Attendance sampleAttendance;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("WorkTrack Inc").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleOffice = OfficeLocation.builder()
                .company(sampleCompany)
                .name("HQ Office")
                .latitude(12.971599)
                .longitude(77.594566)
                .geofenceRadiusM(200.0)
                .build();
        ReflectionTestUtils.setField(sampleOffice, "id", 10L);

        sampleEmployee = Employee.builder()
                .firstName("Bob")
                .lastName("Jones")
                .email("bob@company.com")
                .company(sampleCompany)
                .officeLocation(sampleOffice)
                .build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 100L);

        sampleAttendance = Attendance.builder()
                .attendanceDate(LocalDate.now())
                .checkIn(LocalTime.of(9, 0))
                .status(AttendanceStatus.PRESENT)
                .employee(sampleEmployee)
                .company(sampleCompany)
                .workingHours(8.0)
                .overtimeMinutes(0)
                .late(false)
                .build();
        ReflectionTestUtils.setField(sampleAttendance, "id", 500L);
    }

    private AttendanceCheckInRequest createCheckInRequest() {
        return new AttendanceCheckInRequest(
                12.971599, 77.594566, 10.0, "device-uuid-1", "secret123", null, null, null, null
        );
    }

    private AttendanceCheckOutRequest createCheckOutRequest() {
        return new AttendanceCheckOutRequest(
                12.971599, 77.594566, 10.0, "device-uuid-1", "secret123", null, null
        );
    }

    @Nested
    @DisplayName("Check-In Tests & Order Verification")
    class CheckInTests {

        @Test
        @DisplayName("Device verification MUST occur BEFORE checking duplicate attendance")
        void checkIn_DeviceVerificationOrderingCorrect() {
            AttendanceCheckInRequest request = createCheckInRequest();

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(attendanceRepository.existsByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class))).thenReturn(false);
            when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);
            when(attendanceLogRepository.save(any(AttendanceLog.class))).thenReturn(AttendanceLog.builder().build());

            attendanceService.checkIn(100L, request);

            // InOrder verification asserting device verify happens BEFORE duplicate check
            InOrder inOrder = inOrder(employeeDeviceService, attendanceRepository);
            inOrder.verify(employeeDeviceService).verifyDevice(100L, "device-uuid-1", "secret123");
            inOrder.verify(attendanceRepository).existsByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class));
        }

        @Test
        @DisplayName("Should successfully check in when within geofence radius")
        void checkIn_Success_WithinGeofence() {
            AttendanceCheckInRequest request = createCheckInRequest();

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(attendanceRepository.existsByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class))).thenReturn(false);
            when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);
            when(attendanceLogRepository.save(any(AttendanceLog.class))).thenReturn(AttendanceLog.builder().build());

            AttendanceCheckInResponse response = attendanceService.checkIn(100L, request);

            assertThat(response).isNotNull();
            assertThat(response.withinGeofence()).isTrue();
            assertThat(response.status()).isEqualTo(AttendanceStatus.PRESENT);
        }

        @Test
        @DisplayName("Should throw DuplicateAttendanceException when already checked in today")
        void checkIn_DuplicateCheckIn() {
            AttendanceCheckInRequest request = createCheckInRequest();

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(attendanceRepository.existsByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class))).thenReturn(true);

            assertThatThrownBy(() -> attendanceService.checkIn(100L, request))
                    .isInstanceOf(DuplicateAttendanceException.class)
                    .hasMessageContaining("already exists");
        }
    }

    @Nested
    @DisplayName("Check-Out Tests")
    class CheckOutTests {

        @Test
        @DisplayName("Should successfully check out and update working hours")
        void checkOut_Success() {
            AttendanceCheckOutRequest request = createCheckOutRequest();

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(attendanceRepository.findByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class)))
                    .thenReturn(Optional.of(sampleAttendance));
            when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);
            when(attendanceLogRepository.save(any(AttendanceLog.class))).thenReturn(AttendanceLog.builder().build());

            AttendanceCheckInResponse response = attendanceService.checkOut(100L, request);

            assertThat(response).isNotNull();
            assertThat(sampleAttendance.getCheckOut()).isNotNull();
            verify(attendanceRepository).save(sampleAttendance);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when already checked out")
        void checkOut_AlreadyCheckedOut() {
            sampleAttendance.setCheckOut(LocalTime.of(17, 0));
            AttendanceCheckOutRequest request = createCheckOutRequest();

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(attendanceRepository.findByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class)))
                    .thenReturn(Optional.of(sampleAttendance));

            assertThatThrownBy(() -> attendanceService.checkOut(100L, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already been checked out");
        }
    }

    @Nested
    @DisplayName("Break & Correction Tests")
    class BreakAndCorrectionTests {

        @Test
        @DisplayName("Should start break when checked in and no active break exists")
        void startBreak_Success() {
            BreakRequest request = new BreakRequest(500L);

            when(attendanceRepository.findById(500L)).thenReturn(Optional.of(sampleAttendance));
            when(breakRepository.findFirstByAttendanceIdAndEndAtIsNullOrderByStartAtDesc(500L)).thenReturn(Optional.empty());

            Break savedBreak = Break.builder().attendance(sampleAttendance).startAt(LocalDateTime.now()).build();
            ReflectionTestUtils.setField(savedBreak, "id", 1L);
            when(breakRepository.save(any(Break.class))).thenReturn(savedBreak);

            BreakResponse response = attendanceService.startBreak(100L, request);

            assertThat(response).isNotNull();
            verify(breakRepository).save(any(Break.class));
            verify(attendanceLogRepository).save(any(AttendanceLog.class));
        }

        @Test
        @DisplayName("Should end active break successfully")
        void endBreak_Success() {
            when(attendanceRepository.findByEmployeeIdAndAttendanceDate(eq(100L), any(LocalDate.class)))
                    .thenReturn(Optional.of(sampleAttendance));

            Break activeBreak = Break.builder()
                    .attendance(sampleAttendance)
                    .startAt(LocalDateTime.now().minusMinutes(30))
                    .build();
            ReflectionTestUtils.setField(activeBreak, "id", 1L);

            when(breakRepository.findFirstByAttendanceIdAndEndAtIsNullOrderByStartAtDesc(500L))
                    .thenReturn(Optional.of(activeBreak));
            when(breakRepository.save(any(Break.class))).thenReturn(activeBreak);

            BreakResponse response = attendanceService.endBreak(100L);

            assertThat(response).isNotNull();
            assertThat(activeBreak.getEndAt()).isNotNull();
            assertThat(activeBreak.getDurationMinutes()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should approve manual check-in by manager")
        void approveManualCheckIn_Success() {
            Employee manager = Employee.builder().firstName("Boss").build();
            ReflectionTestUtils.setField(manager, "id", 200L);

            ManualCheckInApprovalRequest request = new ManualCheckInApprovalRequest("Approved by manager on-site", "http://photo.url");

            when(employeeRepository.findById(200L)).thenReturn(Optional.of(manager));
            when(attendanceRepository.findById(500L)).thenReturn(Optional.of(sampleAttendance));
            when(attendanceRepository.save(any(Attendance.class))).thenReturn(sampleAttendance);

            AttendanceResponse response = attendanceService.approveManualCheckIn(500L, 200L, request);

            assertThat(response).isNotNull();
            verify(attendanceLogRepository).save(any(AttendanceLog.class));
        }
    }
}
