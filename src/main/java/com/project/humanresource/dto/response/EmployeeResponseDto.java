package com.project.humanresource.dto.response;

public record EmployeeResponseDto(
        String fullName,
        String emailWork,
        String phoneWork,
        String title,
        boolean active
) {
}
