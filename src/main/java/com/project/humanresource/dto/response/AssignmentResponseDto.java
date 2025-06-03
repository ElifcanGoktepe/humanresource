package com.project.humanresource.dto.response;

import com.project.humanresource.utility.AssignmentCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AssignmentResponseDto(
    Long id,
    String description,
    AssignmentCategory category,
    String serialNumber,
    LocalDate assignmentDate,
    LocalDate returnDate,
    Long employeeId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 