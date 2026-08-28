package com.worktrack.service;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.entity.Attendance;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.repository.PayrollRepository;
import com.worktrack.serviceImpl.ReportExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportExportServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private PayrollRepository payrollRepository;

    @InjectMocks
    private ReportExportServiceImpl reportExportService;

    private Company sampleCompany;
    private Attendance sampleAttendance;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        Employee employee = Employee.builder().build();
        ReflectionTestUtils.setField(employee, "id", 10L);

        sampleAttendance = Attendance.builder()
                .company(sampleCompany)
                .employee(employee)
                .attendanceDate(LocalDate.of(2026, 9, 1))
                .checkIn(LocalTime.of(9, 0))
                .checkOut(LocalTime.of(17, 0))
                .workingHours(8.0)
                .status(AttendanceStatus.PRESENT)
                .build();
        ReflectionTestUtils.setField(sampleAttendance, "id", 100L);
    }

    @Test
    @DisplayName("Should export attendance report CSV successfully")
    void exportAttendanceReportCsv_Success() {
        when(companyRepository.existsById(1L)).thenReturn(true);
        when(attendanceRepository.findByCompanyId(1L)).thenReturn(List.of(sampleAttendance));

        byte[] csvBytes = reportExportService.exportAttendanceReportCsv(1L, null, null);
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        assertThat(csvContent).contains("Attendance ID,Employee ID,Date,Check-In,Check-Out,Working Hours,Status");
        assertThat(csvContent).contains("100,10,2026-09-01,09:00,17:00,8.0,PRESENT");
    }
}
