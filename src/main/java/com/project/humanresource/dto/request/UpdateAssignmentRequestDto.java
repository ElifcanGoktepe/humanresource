package com.project.humanresource.dto.request;

import com.project.humanresource.utility.AssignmentCategory;
import java.time.LocalDate;

public record UpdateAssignmentRequestDto(
    String description,
    AssignmentCategory category,
    String serialNumber,
    LocalDate returnDate
) {} 