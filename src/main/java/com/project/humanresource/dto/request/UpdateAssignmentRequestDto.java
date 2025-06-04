package com.project.humanresource.dto.request;

import com.project.humanresource.utility.AssignmentCategory;
import com.project.humanresource.utility.AssignmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// For updating an existing assignment. Fields are optional.
// Validations apply if a field is provided (not null).
public record UpdateAssignmentRequestDto(
    @NotBlank // Validates only if description is not null
    @Size(min = 3, max = 255)
    String description,

    AssignmentCategory category, // No specific validation, service layer might handle logic

    @FutureOrPresent // Validates only if expectedReturnDate is not null
    LocalDate expectedReturnDate,

    @Size(max = 1000) // Validates only if notes is not null
    String notes,

    AssignmentStatus status, // To manually change the status of an assignment

    @PastOrPresent // Validates only if actualReturnDate is not null
    LocalDate actualReturnDate
) {} 