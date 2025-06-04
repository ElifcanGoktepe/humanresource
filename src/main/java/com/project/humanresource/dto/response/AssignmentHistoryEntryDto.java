package com.project.humanresource.dto.response;

import com.project.humanresource.utility.AssignmentStatus;
import java.time.LocalDateTime;

public record AssignmentHistoryEntryDto(
    LocalDateTime timestamp,
    EmployeeBasicInfoDto changedBy,
    String action,
    AssignmentStatus previousStatus, // Can be null if it's the creation event
    AssignmentStatus newStatus,
    String comment
) {} 