package com.project.humanresource.dto.response;

import java.time.LocalDateTime;

public record LeaveResponseDto(
        Long id,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String description,
        String leaveType,
        String state,
        Long employeeId,
        String firstName,
        String lastName
) {}