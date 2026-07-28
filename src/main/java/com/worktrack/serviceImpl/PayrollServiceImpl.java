package com.worktrack.serviceImpl;

import com.worktrack.constants.PayrollStatus;
import com.worktrack.dto.request.PayrollRequest;
import com.worktrack.dto.response.PayrollResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Payroll;
import com.worktrack.mapper.PayrollMapper;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.PayrollRepository;
import com.worktrack.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    @Override
    public PayrollResponse createPayroll(PayrollRequest request) {

        if (payrollRepository.existsByEmployeeIdAndMonthAndYear(
                request.getEmployeeId(),
                request.getMonth(),
                request.getYear())) {

            throw new RuntimeException(
                    "Payroll already exists for this employee for the selected month and year.");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Payroll payroll = PayrollMapper.toEntity(request, employee, company);

        return PayrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Override
    public PayrollResponse getPayrollById(Long id) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        return PayrollMapper.toResponse(payroll);
    }

    @Override
    public List<PayrollResponse> getAllPayrolls() {

        return payrollRepository.findAll()
                .stream()
                .map(PayrollMapper::toResponse)
                .toList();
    }

    @Override
    public PayrollResponse updatePayroll(Long id, PayrollRequest request) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Payroll updated = PayrollMapper.toEntity(request, employee, company);

        payroll.setMonth(updated.getMonth());
        payroll.setYear(updated.getYear());
        payroll.setBasicSalary(updated.getBasicSalary());
        payroll.setAllowance(updated.getAllowance());
        payroll.setBonus(updated.getBonus());
        payroll.setDeduction(updated.getDeduction());
        payroll.setTax(updated.getTax());
        payroll.setNetSalary(updated.getNetSalary());
        payroll.setEmployee(employee);
        payroll.setCompany(company);

        return PayrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Override
    public PayrollResponse updatePayrollStatus(Long id, PayrollStatus status) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        payroll.setStatus(status);

        return PayrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Override
    public void deletePayroll(Long id) {

        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        payrollRepository.delete(payroll);
    }
}