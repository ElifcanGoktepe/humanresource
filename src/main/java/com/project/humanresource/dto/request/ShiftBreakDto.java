package com.project.humanresource.dto.request;

import java.time.LocalDateTime;

public record ShiftBreakDto(
        LocalDateTime startTime,
        LocalDateTime endTime


) {
}
