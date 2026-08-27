package com.worktrack.service;

import com.worktrack.dto.request.ShiftAssignmentRequest;
import com.worktrack.dto.request.ShiftRequest;
import com.worktrack.dto.response.ShiftAssignmentResponse;
import com.worktrack.dto.response.ShiftResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Shift;
import com.worktrack.entity.ShiftAssignment;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.ShiftAssignmentRepository;
import com.worktrack.repository.ShiftRepository;
import com.worktrack.serviceImpl.ShiftServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ShiftServiceImpl shiftService;

    private Company sampleCompany;
    private Employee sampleEmployee;
    private Shift sampleShift;
    private ShiftAssignment sampleAssignment;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleEmployee = Employee.builder().company(sampleCompany).build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 10L);

        sampleShift = Shift.builder()
                .name("Morning Shift")
                .company(sampleCompany)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .gracePeriodMinutes(15)
                .build();
        ReflectionTestUtils.setField(sampleShift, "id", 100L);

        sampleAssignment = ShiftAssignment.builder()
                .shift(sampleShift)
                .employee(sampleEmployee)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 30))
                .build();
        ReflectionTestUtils.setField(sampleAssignment, "id", 500L);
    }

    @Test
    @DisplayName("Should create shift successfully")
    void createShift_Success() {
        ShiftRequest request = ShiftRequest.builder()
                .name("Morning Shift")
                .companyId(1L)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .gracePeriodMinutes(15)
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(shiftRepository.save(any(Shift.class))).thenReturn(sampleShift);

        ShiftResponse response = shiftService.createShift(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("Morning Shift");
    }

    @Test
    @DisplayName("Should assign shift to employee successfully")
    void assignShiftToEmployee_Success() {
        ShiftAssignmentRequest request = ShiftAssignmentRequest.builder()
                .shiftId(100L)
                .employeeId(10L)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 30))
                .build();

        when(shiftRepository.findById(100L)).thenReturn(Optional.of(sampleShift));
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(sampleEmployee));
        when(shiftAssignmentRepository.save(any(ShiftAssignment.class))).thenReturn(sampleAssignment);

        ShiftAssignmentResponse response = shiftService.assignShiftToEmployee(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(500L);
        assertThat(response.getShiftName()).isEqualTo("Morning Shift");
    }
}
