package com.worktrack.serviceImpl;

import com.worktrack.dto.request.DeviceRegistrationRequest;
import com.worktrack.entity.Employee;
import com.worktrack.entity.EmployeeDevice;
import com.worktrack.repository.EmployeeDeviceRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.service.EmployeeDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeDeviceServiceImpl
        implements EmployeeDeviceService {

    private final EmployeeDeviceRepository employeeDeviceRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EmployeeDevice registerDevice(
            Long employeeId,
            DeviceRegistrationRequest request
    ) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found"
                        )
                );

        if (employeeDeviceRepository
                .existsByEmployeeIdAndDeviceIdAndActiveTrue(
                        employeeId,
                        request.deviceId()
                )) {

            throw new IllegalArgumentException(
                    "Device is already registered"
            );
        }

        EmployeeDevice device = EmployeeDevice.builder()
                .employee(employee)
                .deviceId(request.deviceId())
                .secretHash(
                        passwordEncoder.encode(
                                request.deviceSecret()
                        )
                )
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return employeeDeviceRepository.save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDevice> getEmployeeDevices(
            Long employeeId
    ) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException(
                    "Employee not found"
            );
        }

        return employeeDeviceRepository
                .findByEmployeeId(employeeId);
    }

    @Override
    public void revokeDevice(
            Long employeeId,
            Long deviceId
    ) {

        EmployeeDevice device =
                employeeDeviceRepository.findById(deviceId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Device not found"
                                )
                        );

        if (!device.getEmployee()
                .getId()
                .equals(employeeId)) {

            throw new IllegalArgumentException(
                    "Device does not belong to employee"
            );
        }

        device.setActive(false);
        device.setRevokedAt(LocalDateTime.now());

        employeeDeviceRepository.save(device);
    }
}