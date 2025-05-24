package com.project.humanresource.dto.response;

import com.project.humanresource.utility.AssignmentCategory;

import java.time.LocalDate;

public record AssignmentResponseDto(
        Long id,
        String description,
        AssignmentCategory category,
        String serialNumber,
        LocalDate assignmentDate,
        LocalDate returnDate,
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        boolean isReturned
) {
    public boolean isReturned() {
        return returnDate != null;
    }
} 