package com.worktrack.controller;

import com.worktrack.dto.request.DeviceRegistrationRequest;
import com.worktrack.entity.EmployeeDevice;
import com.worktrack.service.EmployeeDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class EmployeeDeviceController {

    private final EmployeeDeviceService employeeDeviceService;

    @PostMapping("/register/{employeeId}")
    public ResponseEntity<EmployeeDevice> registerDevice(
            @PathVariable Long employeeId,
            @Valid @RequestBody DeviceRegistrationRequest request) {

        return new ResponseEntity<>(
                employeeDeviceService.registerDevice(
                        employeeId,
                        request
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeDevice>> getEmployeeDevices(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeDeviceService.getEmployeeDevices(employeeId)
        );
    }

    @PatchMapping("/{deviceId}/revoke")
    public ResponseEntity<Void> revokeDevice(
            @PathVariable Long deviceId,
            @RequestParam Long employeeId) {

        employeeDeviceService.revokeDevice(
                employeeId,
                deviceId
        );

        return ResponseEntity.noContent().build();
    }
}