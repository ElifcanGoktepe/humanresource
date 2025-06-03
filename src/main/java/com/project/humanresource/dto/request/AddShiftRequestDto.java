package com.project.humanresource.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record AddShiftRequestDto(
        String name,
        String startTime,             // "21:00" gibi → LocalTime.parse(startTime)
        String endTime,
        String description,
        List<ShiftBreakRequest> shiftBreaks,
        Boolean isRecurring,
        List<Integer> daysOfWeek
) {
    public record ShiftBreakRequest(
            String startTime,
            String endTime
    ) {}
}
