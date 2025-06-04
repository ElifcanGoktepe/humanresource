package com.project.humanresource.dto.response;

import com.project.humanresource.utility.AssignmentCategory;
import com.project.humanresource.utility.AssignmentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record AssignmentResponseDto(
    Long id,
    ResourceBasicInfoDto resource,
    EmployeeBasicInfoDto assignedTo,
    EmployeeBasicInfoDto assignedBy,
    String status, // From AssignmentStatus.name()
    String category, // From AssignmentCategory.name()
    String description,
    LocalDate assignmentDate,
    LocalDate expectedReturnDate,
    LocalDate actualReturnDate, // Optional
    String notes, // Optional
    List<AssignmentHistoryEntryDto> history, // Optional, might be fetched on demand
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 