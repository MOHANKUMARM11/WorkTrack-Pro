package com.worktrack.service;

import com.worktrack.dto.response.LeaveBalanceResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.LeaveBalance;
import com.worktrack.entity.LeaveType;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveBalanceRepository;
import com.worktrack.repository.LeaveTypeRepository;
import com.worktrack.serviceImpl.LeaveBalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveBalanceServiceTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @InjectMocks
    private LeaveBalanceServiceImpl leaveBalanceService;

    private Company sampleCompany;
    private Employee sampleEmployee;
    private LeaveType sampleLeaveType;
    private LeaveBalance sampleLeaveBalance;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleEmployee = Employee.builder().firstName("John").lastName("Doe").company(sampleCompany).build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 100L);

        sampleLeaveType = LeaveType.builder().name("Annual Leave").code("ANNUAL").daysAllowedPerYear(15).company(sampleCompany).build();
        ReflectionTestUtils.setField(sampleLeaveType, "id", 10L);

        sampleLeaveBalance = LeaveBalance.builder()
                .employee(sampleEmployee)
                .leaveType(sampleLeaveType)
                .year(LocalDate.now().getYear())
                .allocatedDays(15.0)
                .usedDays(3.0)
                .pendingDays(0.0)
                .remainingDays(12.0)
                .build();
        ReflectionTestUtils.setField(sampleLeaveBalance, "id", 1L);
    }

    @Test
    @DisplayName("Should return leave balances for employee")
    void getEmployeeLeaveBalances_Success() {
        when(employeeRepository.existsById(100L)).thenReturn(true);
        when(leaveBalanceRepository.findByEmployeeIdAndYear(100L, LocalDate.now().getYear()))
                .thenReturn(List.of(sampleLeaveBalance));

        List<LeaveBalanceResponse> responses = leaveBalanceService.getEmployeeLeaveBalances(100L, null);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRemainingDays()).isEqualTo(12.0);
    }

    @Test
    @DisplayName("Should deduct approved leave from remaining balance")
    void deductApprovedLeave_Success() {
        when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
        when(leaveTypeRepository.findByCompanyIdAndCode(1L, "ANNUAL")).thenReturn(Optional.of(sampleLeaveType));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(100L, 10L, LocalDate.now().getYear()))
                .thenReturn(Optional.of(sampleLeaveBalance));

        leaveBalanceService.deductApprovedLeave(100L, "ANNUAL", LocalDate.now().getYear(), 2.0);

        assertThat(sampleLeaveBalance.getUsedDays()).isEqualTo(5.0);
        assertThat(sampleLeaveBalance.getRemainingDays()).isEqualTo(10.0);
        verify(leaveBalanceRepository).save(sampleLeaveBalance);
    }
}
