package com.project.humanresource.dto.request;

public record AddEmployeeForRoleRequirementDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String companyName,
        String titleName
) {
}
