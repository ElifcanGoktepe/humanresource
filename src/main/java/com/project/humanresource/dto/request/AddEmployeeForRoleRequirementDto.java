package com.project.humanresource.dto.request;

public record AddEmployeeForRoleRequirementDto(
        String firstName,
        String lastName,
        String emailWork,
        String phoneWork,
        String companyName,
        String titleName
) {
}
