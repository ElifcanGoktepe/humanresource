package com.project.humanresource.dto.request;

import com.project.humanresource.utility.AssignmentCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateAssignmentRequestDto(
    @NotEmpty String description,
    @NotNull AssignmentCategory category,
    String serialNumber,
    @NotNull @FutureOrPresent LocalDate assignmentDate,
    @NotNull Long employeeId
) {} 