package com.project.humanresource.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateShiftRequestDto(
        Long shiftId,
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String description,
        List<ShiftBreakDto> shiftBreaks, // yeniden eklenen veya güncellenen break'ler
        Boolean isRecurring,
        List<Integer> daysOfWeek

) {
}
