package com.project.humanresource.dto.response;

public record EmployeeResponseDto(
        Long employeeId,
        String fullName,
        String email,
        String phoneNumber,
        String title,
        boolean isActive
) {
}
