package com.project.humanresource.dto.request;

import com.project.humanresource.utility.AssignmentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AssignmentRequestDto(
        @NotBlank(message = "Description is required")
        @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
        String description,

        @NotNull(message = "Category is required")
        AssignmentCategory category,

        @NotBlank(message = "Serial number is required")
        @Size(min = 1, max = 50, message = "Serial number must be between 1 and 50 characters")
        String serialNumber,

        @NotNull(message = "Assignment date is required")
        LocalDate assignmentDate,

        LocalDate returnDate,

        @NotNull(message = "Employee ID is required")
        Long employeeId
) {
} 