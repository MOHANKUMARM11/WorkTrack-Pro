package com.worktrack.serviceImpl;

import com.worktrack.constants.LeaveStatus;
import com.worktrack.dto.request.LeaveRequest;
import com.worktrack.dto.response.LeaveResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Leave;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.DuplicateLeaveException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.exception.custom.LeaveNotFoundException;
import com.worktrack.mapper.LeaveMapper;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.notification.event.LeaveApprovedEvent;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.LeaveRepository;
import com.worktrack.service.LeaveService;
import com.worktrack.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final NotificationEventProducer notificationEventProducer;
    private final LeaveBalanceService leaveBalanceService;

    @Override
    public LeaveResponse createLeave(
            LeaveRequest request) {

        if (leaveRepository.existsByEmployeeIdAndStartDateAndEndDate(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate())) {

            throw new DuplicateLeaveException(
                    "Leave request already exists for these dates.");
        }

        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Employee not found"));

        Company company =
                companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() ->
                                new CompanyNotFoundException(
                                        "Company not found"));

        Leave leave =
                LeaveMapper.toEntity(
                        request,
                        employee,
                        company);

        return LeaveMapper.toResponse(
                leaveRepository.save(leave));
    }

    @Override
    public LeaveResponse getLeaveById(Long id) {

        Leave leave =
                leaveRepository.findById(id)
                        .orElseThrow(() ->
                                new LeaveNotFoundException(
                                        "Leave not found"));

        return LeaveMapper.toResponse(leave);
    }

    @Override
    public List<LeaveResponse> getAllLeaves() {

        return leaveRepository.findAll()
                .stream()
                .map(LeaveMapper::toResponse)
                .toList();
    }

    @Override
    public LeaveResponse updateLeave(
            Long id,
            LeaveRequest request) {

        Leave leave =
                leaveRepository.findById(id)
                        .orElseThrow(() ->
                                new LeaveNotFoundException(
                                        "Leave not found"));

        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(
                                        "Employee not found"));

        Company company =
                companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() ->
                                new CompanyNotFoundException(
                                        "Company not found"));

        Leave updatedLeave =
                LeaveMapper.toEntity(
                        request,
                        employee,
                        company);

        leave.setStartDate(
                updatedLeave.getStartDate());

        leave.setEndDate(
                updatedLeave.getEndDate());

        leave.setTotalDays(
                updatedLeave.getTotalDays());

        leave.setReason(
                updatedLeave.getReason());

        leave.setLeaveType(
                updatedLeave.getLeaveType());

        leave.setEmployee(employee);
        leave.setCompany(company);

        return LeaveMapper.toResponse(
                leaveRepository.save(leave));
    }

    @Override
    public LeaveResponse updateLeaveStatus(
            Long id,
            LeaveStatus status) {

        Leave leave =
                leaveRepository.findById(id)
                        .orElseThrow(() ->
                                new LeaveNotFoundException(
                                        "Leave not found"));

        LeaveStatus previousStatus =
                leave.getStatus();

        leave.setStatus(status);

        Leave savedLeave =
                leaveRepository.save(leave);

        if (status == LeaveStatus.APPROVED
                && previousStatus != LeaveStatus.APPROVED) {

            leaveBalanceService.deductApprovedLeave(
                    savedLeave.getEmployee().getId(),
                    savedLeave.getLeaveType().name(),
                    savedLeave.getStartDate().getYear(),
                    savedLeave.getTotalDays().doubleValue());

            notificationEventProducer.publishLeaveApproved(
                    new LeaveApprovedEvent(
                            savedLeave.getId(),
                            savedLeave.getEmployee().getId(),
                            savedLeave.getTotalDays()));
        }

        return LeaveMapper.toResponse(savedLeave);
    }

    @Override
    public void deleteLeave(Long id) {

        Leave leave =
                leaveRepository.findById(id)
                        .orElseThrow(() ->
                                new LeaveNotFoundException(
                                        "Leave not found"));

        leaveRepository.delete(leave);
    }
}