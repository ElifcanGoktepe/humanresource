package com.project.humanresource.dto.request;

import com.project.humanresource.utility.AssignmentCategory;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

// TODO: Add class-level validator to ensure expectedReturnDate is after or equal to assignmentDate if both are present.
public record CreateAssignmentRequestDto(
    @NotNull @Positive
    Long resourceId, // ID of the Resource to be assigned

    @NotNull @Positive
    Long assignedToEmployeeId, // ID of the Employee to whom the resource is assigned

    @NotNull // Assuming assignmentDate is always required
    @FutureOrPresent // Ensures the date is not in the past
    LocalDate assignmentDate,

    @NotNull // Assuming expectedReturnDate is always required for new assignments
    @FutureOrPresent // Ensures the date is not in the past
    LocalDate expectedReturnDate,

    @NotBlank
    @Size(min = 3, max = 255)
    String description, // General description of the assignment itself

    @NotNull
    AssignmentCategory category, // Category of the assignment (e.g., TEMPORARY, PROJECT_BASED)
                                 // This is distinct from Resource's category
    @Size(max = 1000)
    String notes // Optional detailed notes about the assignment
) {} 