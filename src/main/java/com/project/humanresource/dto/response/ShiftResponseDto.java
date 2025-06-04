package com.project.humanresource.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ShiftResponseDto(
        Long id,
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description,
        Boolean isRecurring,
        List<Integer> daysOfWeek,
        List<Long> shiftBreakIds,
        List<Long> employeeIds



) {
}