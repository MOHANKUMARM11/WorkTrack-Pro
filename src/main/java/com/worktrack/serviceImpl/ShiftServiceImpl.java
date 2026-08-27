package com.worktrack.serviceImpl;

import com.worktrack.dto.request.ShiftAssignmentRequest;
import com.worktrack.dto.request.ShiftRequest;
import com.worktrack.dto.response.ShiftAssignmentResponse;
import com.worktrack.dto.response.ShiftResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Shift;
import com.worktrack.entity.ShiftAssignment;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.EmployeeNotFoundException;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.ShiftAssignmentRepository;
import com.worktrack.repository.ShiftRepository;
import com.worktrack.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public ShiftResponse createShift(ShiftRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        Shift shift = Shift.builder()
                .name(request.getName())
                .company(company)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .gracePeriodMinutes(request.getGracePeriodMinutes() != null ? request.getGracePeriodMinutes() : 15)
                .build();

        return mapToShiftResponse(shiftRepository.save(shift));
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResponse getShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
        return mapToShiftResponse(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getShiftsByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return shiftRepository.findByCompanyId(companyId).stream()
                .map(this::mapToShiftResponse)
                .toList();
    }

    @Override
    public ShiftAssignmentResponse assignShiftToEmployee(ShiftAssignmentRequest request) {
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + request.getShiftId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        ShiftAssignment assignment = ShiftAssignment.builder()
                .shift(shift)
                .employee(employee)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return mapToAssignmentResponse(shiftAssignmentRepository.save(assignment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftAssignmentResponse> getAssignmentsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeNotFoundException("Employee not found");
        }
        return shiftAssignmentRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToAssignmentResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftAssignmentResponse getActiveAssignmentForEmployeeOnDate(Long employeeId, LocalDate date) {
        ShiftAssignment assignment = shiftAssignmentRepository.findActiveAssignmentForEmployeeOnDate(employeeId, date)
                .orElseThrow(() -> new ResourceNotFoundException("No active shift assignment found for employee on date: " + date));
        return mapToAssignmentResponse(assignment);
    }

    @Override
    public void deleteShift(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));
        shiftRepository.delete(shift);
    }

    private ShiftResponse mapToShiftResponse(Shift shift) {
        return ShiftResponse.builder()
                .id(shift.getId())
                .name(shift.getName())
                .companyId(shift.getCompany().getId())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .gracePeriodMinutes(shift.getGracePeriodMinutes())
                .createdAt(shift.getCreatedAt())
                .updatedAt(shift.getUpdatedAt())
                .build();
    }

    private ShiftAssignmentResponse mapToAssignmentResponse(ShiftAssignment sa) {
        String empName = (sa.getEmployee().getFirstName() != null)
                ? sa.getEmployee().getFirstName() + " " + sa.getEmployee().getLastName()
                : "Employee #" + sa.getEmployee().getId();
        return ShiftAssignmentResponse.builder()
                .id(sa.getId())
                .shiftId(sa.getShift().getId())
                .shiftName(sa.getShift().getName())
                .employeeId(sa.getEmployee().getId())
                .employeeName(empName)
                .startDate(sa.getStartDate())
                .endDate(sa.getEndDate())
                .createdAt(sa.getCreatedAt())
                .build();
    }
}
