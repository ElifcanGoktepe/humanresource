package com.project.humanresource.dto.response;

public record EmployeeResponseDto(
        String fullName,
        String email,
        String phoneNumber,
        String title,
        boolean active
) {
}
