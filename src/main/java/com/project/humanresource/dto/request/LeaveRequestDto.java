package com.project.humanresource.dto.request;

import com.project.humanresource.utility.LeaveTypes;

import java.time.LocalDateTime;

public record LeaveRequestDto(
        LocalDateTime startDate,
        LocalDateTime endDate,
        String description,
        LeaveTypes leaveType
) {
}
