package com.worktrack.scheduler;

import com.worktrack.entity.LeaveBalance;
import com.worktrack.repository.LeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveAccrualSchedulerTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @InjectMocks
    private LeaveAccrualScheduler leaveAccrualScheduler;

    private LeaveBalance sampleBalance;

    @BeforeEach
    void setUp() {
        sampleBalance = LeaveBalance.builder()
                .allocatedDays(12.0)
                .usedDays(1.0)
                .pendingDays(0.0)
                .remainingDays(11.0)
                .build();
        ReflectionTestUtils.setField(sampleBalance, "id", 50L);
    }

    @Test
    @DisplayName("Should accrue leave balance monthly")
    void processMonthlyLeaveAccrual_Success() {
        when(leaveBalanceRepository.findAll()).thenReturn(List.of(sampleBalance));

        int accrued = leaveAccrualScheduler.processMonthlyLeaveAccrual();

        assertThat(accrued).isEqualTo(1);
        assertThat(sampleBalance.getAllocatedDays()).isEqualTo(13.5);
        assertThat(sampleBalance.getRemainingDays()).isEqualTo(12.5);
        verify(leaveBalanceRepository, times(1)).save(sampleBalance);
    }
}
