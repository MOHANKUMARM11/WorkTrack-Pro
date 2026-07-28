package com.worktrack.serviceImpl;

import com.worktrack.constants.AttendanceStatus;
import com.worktrack.constants.LeaveStatus;
import com.worktrack.constants.PayrollStatus;
import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.response.DashboardResponse;
import com.worktrack.repository.AttendanceRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.repository.PayrollRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final TaskRepository taskRepository;
    private final PayrollRepository payrollRepository;

    @Override
    public DashboardResponse getDashboard() {

        return DashboardResponse.builder()
                .totalCompanies(companyRepository.count())
                .totalEmployees(employeeRepository.count())

                .presentToday(
                        attendanceRepository.countByAttendanceDateAndStatus(
                                LocalDate.now(),
                                AttendanceStatus.PRESENT
                        )
                )

                .absentToday(
                        attendanceRepository.countByAttendanceDateAndStatus(
                                LocalDate.now(),
                                AttendanceStatus.ABSENT
                        )
                )

                .pendingLeaves(
                        leaveRepository.countByStatus(
                                LeaveStatus.PENDING
                        )
                )

                .approvedLeaves(
                        leaveRepository.countByStatus(
                                LeaveStatus.APPROVED
                        )
                )

                .totalTasks(taskRepository.count())

                .completedTasks(
                        taskRepository.countByStatus(
                                TaskStatus.COMPLETED
                        )
                )

                .pendingTasks(
                        taskRepository.countByStatus(
                                TaskStatus.TODO
                        )
                )

                .totalPayrolls(payrollRepository.count())

                .generatedPayrolls(
                        payrollRepository.countByStatus(
                                PayrollStatus.GENERATED
                        )
                )

                .build();
    }
}