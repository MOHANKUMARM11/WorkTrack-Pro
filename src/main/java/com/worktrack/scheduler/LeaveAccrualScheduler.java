package com.worktrack.scheduler;

import com.worktrack.entity.LeaveBalance;
import com.worktrack.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveAccrualScheduler {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Scheduled(cron = "0 0 0 1 * *") // Runs at 00:00 AM on the 1st of every month
    @Transactional
    public int processMonthlyLeaveAccrual() {
        log.info("Running LeaveAccrualScheduler for monthly leave accrual...");

        List<LeaveBalance> balances = leaveBalanceRepository.findAll();
        int accruedCount = 0;

        for (LeaveBalance balance : balances) {
            double currentAllocated = balance.getAllocatedDays() != null ? balance.getAllocatedDays() : 0.0;
            double currentRemaining = balance.getRemainingDays() != null ? balance.getRemainingDays() : 0.0;

            balance.setAllocatedDays(currentAllocated + 1.5);
            balance.setRemainingDays(currentRemaining + 1.5);

            leaveBalanceRepository.save(balance);
            accruedCount++;
        }

        log.info("LeaveAccrualScheduler completed. Accrued leave balances for {} records.", accruedCount);
        return accruedCount;
    }
}
