package com.worktrack.scheduler;

import com.worktrack.entity.Attendance;
import com.worktrack.entity.Employee;
import com.worktrack.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceAutoCheckoutSchedulerTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceAutoCheckoutScheduler autoCheckoutScheduler;

    private Attendance unclosedAttendance;

    @BeforeEach
    void setUp() {
        Employee employee = Employee.builder().build();
        ReflectionTestUtils.setField(employee, "id", 10L);

        unclosedAttendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(LocalDate.now().minusDays(1))
                .checkIn(LocalTime.of(9, 0))
                .checkOut(null)
                .build();
        ReflectionTestUtils.setField(unclosedAttendance, "id", 100L);
    }

    @Test
    @DisplayName("Should process auto checkout for unclosed attendance records")
    void processAutoCheckouts_Success() {
        when(attendanceRepository.findByCheckOutIsNullAndAttendanceDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(unclosedAttendance));

        int processed = autoCheckoutScheduler.processAutoCheckouts();

        assertThat(processed).isEqualTo(1);
        verify(attendanceRepository, times(1)).save(unclosedAttendance);
        assertThat(unclosedAttendance.getCheckOut()).isNotNull();
    }
}
