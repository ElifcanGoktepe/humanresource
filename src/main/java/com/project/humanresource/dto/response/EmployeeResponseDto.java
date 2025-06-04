package com.project.humanresource.dto.response;

public record EmployeeResponseDto(
        String fullName,
        String Email,
        String PhoneNumber,
        String title,
        boolean active
) {
}
