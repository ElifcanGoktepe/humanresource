package com.project.humanresource.dto.request;

public record AddEmployeeForRoleRequirementDto(
        String firstName,
        String lastName,
        String email,
        String phoneWork,
        String companyName,
        String titleName
) {
}
