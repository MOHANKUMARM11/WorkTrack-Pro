package com.worktrack.serviceImpl;

import com.worktrack.entity.Attendance;
import com.worktrack.entity.Leave;
import com.worktrack.entity.Payroll;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.repository.PayrollRepository;
import com.worktrack.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportExportServiceImpl implements ReportExportService {

    private final CompanyRepository companyRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final PayrollRepository payrollRepository;

    @Override
    public byte[] exportAttendanceReportCsv(Long companyId, LocalDate startDate, LocalDate endDate) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        List<Attendance> records = attendanceRepository.findByCompanyId(companyId).stream()
                .filter(a -> (startDate == null || !a.getAttendanceDate().isBefore(startDate)) &&
                             (endDate == null || !a.getAttendanceDate().isAfter(endDate)))
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("Attendance ID,Employee ID,Date,Check-In,Check-Out,Working Hours,Status\n");

        for (Attendance a : records) {
            csv.append(a.getId()).append(",")
               .append(a.getEmployee().getId()).append(",")
               .append(a.getAttendanceDate()).append(",")
               .append(a.getCheckIn() != null ? a.getCheckIn() : "").append(",")
               .append(a.getCheckOut() != null ? a.getCheckOut() : "").append(",")
               .append(a.getWorkingHours() != null ? a.getWorkingHours() : 0.0).append(",")
               .append(a.getStatus()).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportLeaveReportCsv(Long companyId, LocalDate startDate, LocalDate endDate) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        List<Leave> leaves = leaveRepository.findByCompanyId(companyId).stream()
                .filter(l -> (startDate == null || !l.getStartDate().isBefore(startDate)) &&
                             (endDate == null || !l.getEndDate().isAfter(endDate)))
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("Leave ID,Employee ID,Leave Type,Start Date,End Date,Status,Reason\n");

        for (Leave l : leaves) {
            String leaveTypeName = (l.getLeaveType() != null) ? l.getLeaveType().name() : "GENERAL";
            csv.append(l.getId()).append(",")
               .append(l.getEmployee().getId()).append(",")
               .append(leaveTypeName).append(",")
               .append(l.getStartDate()).append(",")
               .append(l.getEndDate()).append(",")
               .append(l.getStatus()).append(",")
               .append(l.getReason() != null ? "\"" + l.getReason().replace("\"", "\"\"") + "\"" : "").append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] exportPayrollReportCsv(Long companyId, Integer year, Integer month) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        List<Payroll> payrolls = payrollRepository.findByCompanyId(companyId).stream()
                .filter(p -> (year == null || year.equals(p.getYear())) &&
                             (month == null || month.equals(p.getMonth())))
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("Payroll ID,Employee ID,Year,Month,Base Salary,Allowance,Bonus,Deduction,Net Salary\n");

        for (Payroll p : payrolls) {
            csv.append(p.getId()).append(",")
               .append(p.getEmployee().getId()).append(",")
               .append(p.getYear()).append(",")
               .append(p.getMonth()).append(",")
               .append(p.getBasicSalary()).append(",")
               .append(p.getAllowance()).append(",")
               .append(p.getBonus()).append(",")
               .append(p.getDeduction()).append(",")
               .append(p.getNetSalary()).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
