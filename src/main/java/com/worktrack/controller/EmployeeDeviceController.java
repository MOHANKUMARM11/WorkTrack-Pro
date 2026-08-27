package com.worktrack.controller;

import com.worktrack.constants.UserRole;
import com.worktrack.dto.request.DeviceRegistrationRequest;
import com.worktrack.dto.response.DeviceResponse;
import com.worktrack.entity.Employee;
import com.worktrack.entity.EmployeeDevice;
import com.worktrack.entity.User;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.EmployeeDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class EmployeeDeviceController {

    private final EmployeeDeviceService employeeDeviceService;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/register/{employeeId}")
    public ResponseEntity<DeviceResponse> registerDevice(
            @PathVariable Long employeeId,
            @Valid @RequestBody DeviceRegistrationRequest request,
            Authentication authentication) {

        validateEmployeeAccess(employeeId, authentication);

        EmployeeDevice device = employeeDeviceService.registerDevice(
                employeeId,
                request
        );

        return new ResponseEntity<>(
                toDeviceResponse(device),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<DeviceResponse>> getEmployeeDevices(
            @PathVariable Long employeeId,
            Authentication authentication) {

        validateEmployeeAccess(employeeId, authentication);

        List<DeviceResponse> responseList = employeeDeviceService.getEmployeeDevices(employeeId)
                .stream()
                .map(this::toDeviceResponse)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @PatchMapping("/{deviceId}/revoke")
    public ResponseEntity<Void> revokeDevice(
            @PathVariable Long deviceId,
            @RequestParam Long employeeId,
            Authentication authentication) {

        validateEmployeeAccess(employeeId, authentication);

        employeeDeviceService.revokeDevice(
                employeeId,
                deviceId
        );

        return ResponseEntity.noContent().build();
    }

    private void validateEmployeeAccess(Long targetEmployeeId, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            if (user.getRole() == UserRole.EMPLOYEE) {
                Employee employee = employeeRepository.findByEmail(user.getEmail()).orElse(null);
                if (employee != null && !employee.getId().equals(targetEmployeeId)) {
                    throw new AccessDeniedException("Cannot perform device operations for another employee");
                }
            }
        }
    }

    private DeviceResponse toDeviceResponse(EmployeeDevice device) {
        return new DeviceResponse(
                device.getId(),
                device.getDeviceId(),
                device.getActive(),
                device.getCreatedAt(),
                device.getRevokedAt()
        );
    }
}