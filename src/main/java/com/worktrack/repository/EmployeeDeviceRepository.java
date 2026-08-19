package com.worktrack.repository;

import com.worktrack.entity.EmployeeDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeDeviceRepository
        extends JpaRepository<EmployeeDevice, Long> {

    List<EmployeeDevice> findByEmployeeId(Long employeeId);

    Optional<EmployeeDevice> findByEmployeeIdAndDeviceId(
            Long employeeId,
            String deviceId
    );

    Optional<EmployeeDevice> findByEmployeeIdAndDeviceIdAndActiveTrue(
            Long employeeId,
            String deviceId
    );

    boolean existsByEmployeeIdAndDeviceIdAndActiveTrue(
            Long employeeId,
            String deviceId
    );
}