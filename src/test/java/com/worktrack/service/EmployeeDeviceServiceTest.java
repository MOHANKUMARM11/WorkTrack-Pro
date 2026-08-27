package com.worktrack.service;

import com.worktrack.dto.request.DeviceRegistrationRequest;
import com.worktrack.entity.Employee;
import com.worktrack.entity.EmployeeDevice;
import com.worktrack.repository.EmployeeDeviceRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.serviceImpl.EmployeeDeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeDeviceServiceTest {

    @Mock
    private EmployeeDeviceRepository employeeDeviceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeDeviceServiceImpl employeeDeviceService;

    private Employee sampleEmployee;

    @BeforeEach
    void setUp() {
        sampleEmployee = Employee.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@company.com")
                .build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 100L);
    }

    @Nested
    @DisplayName("Device Registration Tests")
    class RegisterDeviceTests {

        @Test
        @DisplayName("Should successfully register a new device")
        void registerDevice_Success() {
            DeviceRegistrationRequest request = new DeviceRegistrationRequest("device-abc-123", "secret123");

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(employeeDeviceRepository.existsByEmployeeIdAndDeviceIdAndActiveTrue(100L, "device-abc-123")).thenReturn(false);
            when(passwordEncoder.encode("secret123")).thenReturn("hashedSecret");

            EmployeeDevice savedDevice = EmployeeDevice.builder()
                    .employee(sampleEmployee)
                    .deviceId("device-abc-123")
                    .secretHash("hashedSecret")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            ReflectionTestUtils.setField(savedDevice, "id", 1L);

            when(employeeDeviceRepository.save(any(EmployeeDevice.class))).thenReturn(savedDevice);

            EmployeeDevice result = employeeDeviceService.registerDevice(100L, request);

            assertThat(result).isNotNull();
            assertThat(result.getDeviceId()).isEqualTo("device-abc-123");
            assertThat(result.getActive()).isTrue();
            verify(employeeDeviceRepository).save(any(EmployeeDevice.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when device already registered")
        void registerDevice_AlreadyRegistered() {
            DeviceRegistrationRequest request = new DeviceRegistrationRequest("device-abc-123", "secret123");

            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(employeeDeviceRepository.existsByEmployeeIdAndDeviceIdAndActiveTrue(100L, "device-abc-123")).thenReturn(true);

            assertThatThrownBy(() -> employeeDeviceService.registerDevice(100L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device is already registered");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when employee not found")
        void registerDevice_EmployeeNotFound() {
            DeviceRegistrationRequest request = new DeviceRegistrationRequest("device-abc-123", "secret123");

            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeDeviceService.registerDevice(999L, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Employee not found");
        }
    }

    @Nested
    @DisplayName("Device Verification Tests")
    class VerifyDeviceTests {

        @Test
        @DisplayName("Should successfully verify active device with matching secret")
        void verifyDevice_Success() {
            EmployeeDevice device = EmployeeDevice.builder()
                    .employee(sampleEmployee)
                    .deviceId("device-abc-123")
                    .secretHash("hashedSecret")
                    .active(true)
                    .build();

            when(employeeDeviceRepository.findByEmployeeIdAndDeviceId(100L, "device-abc-123"))
                    .thenReturn(Optional.of(device));
            when(passwordEncoder.matches("secret123", "hashedSecret")).thenReturn(true);

            EmployeeDevice result = employeeDeviceService.verifyDevice(100L, "device-abc-123", "secret123");

            assertThat(result).isNotNull();
            assertThat(result.getDeviceId()).isEqualTo("device-abc-123");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when device secret is wrong")
        void verifyDevice_WrongSecret() {
            EmployeeDevice device = EmployeeDevice.builder()
                    .employee(sampleEmployee)
                    .deviceId("device-abc-123")
                    .secretHash("hashedSecret")
                    .active(true)
                    .build();

            when(employeeDeviceRepository.findByEmployeeIdAndDeviceId(100L, "device-abc-123"))
                    .thenReturn(Optional.of(device));
            when(passwordEncoder.matches("wrongSecret", "hashedSecret")).thenReturn(false);

            assertThatThrownBy(() -> employeeDeviceService.verifyDevice(100L, "device-abc-123", "wrongSecret"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid device credentials");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when device is inactive or revoked")
        void verifyDevice_InactiveDevice() {
            EmployeeDevice revokedDevice = EmployeeDevice.builder()
                    .employee(sampleEmployee)
                    .deviceId("device-abc-123")
                    .secretHash("hashedSecret")
                    .active(false)
                    .revokedAt(LocalDateTime.now())
                    .build();

            when(employeeDeviceRepository.findByEmployeeIdAndDeviceId(100L, "device-abc-123"))
                    .thenReturn(Optional.of(revokedDevice));

            assertThatThrownBy(() -> employeeDeviceService.verifyDevice(100L, "device-abc-123", "secret123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device is inactive or revoked");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when deviceId or secret is blank")
        void verifyDevice_MissingArgs() {
            assertThatThrownBy(() -> employeeDeviceService.verifyDevice(100L, "", "secret123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device ID is required");

            assertThatThrownBy(() -> employeeDeviceService.verifyDevice(100L, "device-1", ""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device secret is required");
        }
    }

    @Nested
    @DisplayName("Device Revocation Tests")
    class RevokeDeviceTests {

        @Test
        @DisplayName("Should revoke device successfully")
        void revokeDevice_Success() {
            EmployeeDevice device = EmployeeDevice.builder()
                    .employee(sampleEmployee)
                    .deviceId("device-abc-123")
                    .active(true)
                    .build();
            ReflectionTestUtils.setField(device, "id", 1L);

            when(employeeDeviceRepository.findById(1L)).thenReturn(Optional.of(device));

            employeeDeviceService.revokeDevice(100L, 1L);

            assertThat(device.getActive()).isFalse();
            assertThat(device.getRevokedAt()).isNotNull();
            verify(employeeDeviceRepository).save(device);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when device does not belong to employee")
        void revokeDevice_WrongOwner() {
            Employee otherEmployee = Employee.builder().build();
            ReflectionTestUtils.setField(otherEmployee, "id", 200L);

            EmployeeDevice device = EmployeeDevice.builder()
                    .employee(otherEmployee)
                    .deviceId("device-abc-123")
                    .active(true)
                    .build();
            ReflectionTestUtils.setField(device, "id", 1L);

            when(employeeDeviceRepository.findById(1L)).thenReturn(Optional.of(device));

            assertThatThrownBy(() -> employeeDeviceService.revokeDevice(100L, 1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Device does not belong to employee");
        }
    }
}
