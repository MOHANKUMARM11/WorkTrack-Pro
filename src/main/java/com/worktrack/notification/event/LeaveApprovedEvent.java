package com.worktrack.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class LeaveApprovedEvent {

    private final Long leaveId;
    private final Long employeeId;
    private final Integer totalDays;

    public LeaveApprovedEvent(
            Long leaveId,
            Long employeeId,
            Integer totalDays) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.totalDays = totalDays;
    }

    public Long getLeaveId() {
        return leaveId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Integer getTotalDays() {
        return totalDays;
    }
}