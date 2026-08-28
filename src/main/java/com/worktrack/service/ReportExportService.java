package com.worktrack.service;

import java.time.LocalDate;

public interface ReportExportService {

    byte[] exportAttendanceReportCsv(Long companyId, LocalDate startDate, LocalDate endDate);

    byte[] exportLeaveReportCsv(Long companyId, LocalDate startDate, LocalDate endDate);

    byte[] exportPayrollReportCsv(Long companyId, Integer year, Integer month);
}
