package com.worktrack.mapper;

import com.worktrack.constants.PayrollStatus;
import com.worktrack.dto.request.PayrollRequest;
import com.worktrack.dto.response.PayrollResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Payroll;

import java.math.BigDecimal;

public class PayrollMapper {

    public static Payroll toEntity(
            PayrollRequest request,
            Employee employee,
            Company company) {

        BigDecimal allowance = request.getAllowance() == null ? BigDecimal.ZERO : request.getAllowance();
        BigDecimal bonus = request.getBonus() == null ? BigDecimal.ZERO : request.getBonus();
        BigDecimal deduction = request.getDeduction() == null ? BigDecimal.ZERO : request.getDeduction();
        BigDecimal tax = request.getTax() == null ? BigDecimal.ZERO : request.getTax();

        BigDecimal netSalary = request.getBasicSalary()
                .add(allowance)
                .add(bonus)
                .subtract(deduction)
                .subtract(tax);

        return Payroll.builder()
                .month(request.getMonth())
                .year(request.getYear())
                .basicSalary(request.getBasicSalary())
                .allowance(allowance)
                .bonus(bonus)
                .deduction(deduction)
                .tax(tax)
                .netSalary(netSalary)
                .status(PayrollStatus.PENDING)
                .employee(employee)
                .company(company)
                .build();
    }

    public static PayrollResponse toResponse(Payroll payroll) {

        return PayrollResponse.builder()
                .id(payroll.getId())
                .month(payroll.getMonth())
                .year(payroll.getYear())
                .basicSalary(payroll.getBasicSalary())
                .allowance(payroll.getAllowance())
                .bonus(payroll.getBonus())
                .deduction(payroll.getDeduction())
                .tax(payroll.getTax())
                .netSalary(payroll.getNetSalary())
                .status(payroll.getStatus())
                .employeeId(payroll.getEmployee().getId())
                .employeeName(
                        payroll.getEmployee().getFirstName() + " " +
                                payroll.getEmployee().getLastName()
                )
                .companyId(payroll.getCompany().getId())
                .companyName(payroll.getCompany().getName())
                .createdAt(payroll.getCreatedAt())
                .updatedAt(payroll.getUpdatedAt())
                .build();
    }
}