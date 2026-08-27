package com.worktrack.service;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.constants.LeaveType;
import com.worktrack.dto.request.LeaveRequest;
import com.worktrack.dto.response.LeaveResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Leave;
import com.worktrack.exception.custom.DuplicateLeaveException;
import com.worktrack.exception.custom.LeaveNotFoundException;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.serviceImpl.LeaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private NotificationEventProducer notificationEventProducer;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private Company sampleCompany;
    private Employee sampleEmployee;
    private Leave sampleLeave;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Corp").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@acme.com")
                .company(sampleCompany)
                .build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 100L);

        sampleLeave = Leave.builder()
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .totalDays(3)
                .reason("Vacation")
                .leaveType(LeaveType.ANNUAL)
                .status(LeaveStatus.PENDING)
                .employee(sampleEmployee)
                .company(sampleCompany)
                .build();
        ReflectionTestUtils.setField(sampleLeave, "id", 50L);
    }

    private LeaveRequest createLeaveRequest() {
        LeaveRequest request = new LeaveRequest();
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setReason("Vacation");
        request.setLeaveType(LeaveType.ANNUAL);
        request.setEmployeeId(100L);
        request.setCompanyId(1L);
        return request;
    }

    @Nested
    @DisplayName("Create Leave Tests")
    class CreateLeaveTests {

        @Test
        @DisplayName("Should successfully create a leave request")
        void createLeave_Success() {
            LeaveRequest request = createLeaveRequest();

            when(leaveRepository.existsByEmployeeIdAndStartDateAndEndDate(100L, request.getStartDate(), request.getEndDate())).thenReturn(false);
            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(leaveRepository.save(any(Leave.class))).thenReturn(sampleLeave);

            LeaveResponse response = leaveService.createLeave(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(50L);
            assertThat(response.getStatus()).isEqualTo(LeaveStatus.PENDING);
        }

        @Test
        @DisplayName("Should throw DuplicateLeaveException when duplicate dates requested")
        void createLeave_DuplicateDates() {
            LeaveRequest request = createLeaveRequest();

            when(leaveRepository.existsByEmployeeIdAndStartDateAndEndDate(100L, request.getStartDate(), request.getEndDate())).thenReturn(true);

            assertThatThrownBy(() -> leaveService.createLeave(request))
                    .isInstanceOf(DuplicateLeaveException.class);
        }
    }

    @Nested
    @DisplayName("Update Leave Status Tests")
    class UpdateLeaveStatusTests {

        @Test
        @DisplayName("Should update status to APPROVED and trigger balance deduction & notification")
        void updateLeaveStatus_Approve_TriggersDeduction() {
            when(leaveRepository.findById(50L)).thenReturn(Optional.of(sampleLeave));
            when(leaveRepository.save(any(Leave.class))).thenReturn(sampleLeave);

            LeaveResponse response = leaveService.updateLeaveStatus(50L, LeaveStatus.APPROVED);

            assertThat(response).isNotNull();
            assertThat(sampleLeave.getStatus()).isEqualTo(LeaveStatus.APPROVED);

            verify(leaveBalanceService).deductApprovedLeave(100L, "ANNUAL", sampleLeave.getStartDate().getYear(), 3.0);
            verify(notificationEventProducer).publishLeaveApproved(any());
        }

        @Test
        @DisplayName("Should throw LeaveNotFoundException when leave ID invalid")
        void updateLeaveStatus_NotFound() {
            when(leaveRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> leaveService.updateLeaveStatus(999L, LeaveStatus.APPROVED))
                    .isInstanceOf(LeaveNotFoundException.class);
        }
    }
}
