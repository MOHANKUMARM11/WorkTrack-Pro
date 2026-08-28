package com.worktrack.service;

import com.worktrack.constants.PayrollStatus;
import com.worktrack.dto.request.PayrollRequest;
import com.worktrack.dto.response.PayrollResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Payroll;
import com.worktrack.exception.custom.DuplicatePayrollException;
import com.worktrack.exception.custom.PayrollNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.PayrollRepository;
import com.worktrack.serviceImpl.PayrollServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private PayrollServiceImpl payrollService;

    private Company sampleCompany;
    private Employee sampleEmployee;
    private Payroll samplePayroll;
    private PayrollRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Corp").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleEmployee = Employee.builder().build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 10L);

        samplePayroll = Payroll.builder()
                .month(9)
                .year(2026)
                .basicSalary(BigDecimal.valueOf(5000.00))
                .allowance(BigDecimal.valueOf(500.00))
                .bonus(BigDecimal.valueOf(200.00))
                .deduction(BigDecimal.valueOf(100.00))
                .tax(BigDecimal.valueOf(300.00))
                .netSalary(BigDecimal.valueOf(5300.00))
                .status(PayrollStatus.PENDING)
                .employee(sampleEmployee)
                .company(sampleCompany)
                .build();
        ReflectionTestUtils.setField(samplePayroll, "id", 100L);

        sampleRequest = new PayrollRequest();
        sampleRequest.setEmployeeId(10L);
        sampleRequest.setCompanyId(1L);
        sampleRequest.setMonth(9);
        sampleRequest.setYear(2026);
        sampleRequest.setBasicSalary(BigDecimal.valueOf(5000.00));
        sampleRequest.setAllowance(BigDecimal.valueOf(500.00));
        sampleRequest.setBonus(BigDecimal.valueOf(200.00));
        sampleRequest.setDeduction(BigDecimal.valueOf(100.00));
        sampleRequest.setTax(BigDecimal.valueOf(300.00));
    }

    @Test
    @DisplayName("Should create payroll successfully")
    void createPayroll_Success() {
        when(payrollRepository.existsByEmployeeIdAndMonthAndYear(10L, 9, 2026)).thenReturn(false);
        when(employeeRepository.findById(10L)).thenReturn(java.util.Optional.of(sampleEmployee));
        when(companyRepository.findById(1L)).thenReturn(java.util.Optional.of(sampleCompany));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(samplePayroll);

        PayrollResponse response = payrollService.createPayroll(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getBasicSalary()).isEqualByComparingTo(BigDecimal.valueOf(5000.00));
        verify(payrollRepository).save(any(Payroll.class));
    }

    @Test
    @DisplayName("Should throw DuplicatePayrollException when payroll exists")
    void createPayroll_Duplicate_ThrowsException() {
        when(payrollRepository.existsByEmployeeIdAndMonthAndYear(10L, 9, 2026)).thenReturn(true);

        assertThatThrownBy(() -> payrollService.createPayroll(sampleRequest))
                .isInstanceOf(DuplicatePayrollException.class);
    }

    @Test
    @DisplayName("Should get payroll by ID")
    void getPayrollById_Success() {
        when(payrollRepository.findById(100L)).thenReturn(java.util.Optional.of(samplePayroll));

        PayrollResponse response = payrollService.getPayrollById(100L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should throw PayrollNotFoundException when payroll ID not found")
    void getPayrollById_NotFound_ThrowsException() {
        when(payrollRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> payrollService.getPayrollById(999L))
                .isInstanceOf(PayrollNotFoundException.class);
    }

    @Test
    @DisplayName("Should update payroll status")
    void updatePayrollStatus_Success() {
        when(payrollRepository.findById(100L)).thenReturn(java.util.Optional.of(samplePayroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(samplePayroll);

        PayrollResponse response = payrollService.updatePayrollStatus(100L, PayrollStatus.PAID);

        assertThat(response).isNotNull();
        verify(payrollRepository).save(samplePayroll);
    }

    @Test
    @DisplayName("Should delete payroll successfully")
    void deletePayroll_Success() {
        when(payrollRepository.findById(100L)).thenReturn(java.util.Optional.of(samplePayroll));

        payrollService.deletePayroll(100L);

        verify(payrollRepository).delete(samplePayroll);
    }
}
