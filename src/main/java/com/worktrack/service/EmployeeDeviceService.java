package com.worktrack.service;

import com.worktrack.dto.request.DeviceRegistrationRequest;
import com.worktrack.entity.EmployeeDevice;

import java.util.List;

public interface EmployeeDeviceService {

    EmployeeDevice registerDevice(
            Long employeeId,
            DeviceRegistrationRequest request
    );

    List<EmployeeDevice> getEmployeeDevices(Long employeeId);

    void revokeDevice(
            Long employeeId,
            Long deviceId
    );

    EmployeeDevice verifyDevice(
            Long employeeId,
            String deviceId,
            String deviceSecret
    );
}