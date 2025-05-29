package com.project.humanresource.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record AddEmployeeRequestDto(
        @NotNull
        String name,
        @NotNull
        String surname,
        @Email
        String email,
        @Pattern(regexp = "^\\d{11}$")
        String phoneNumber,
        @NotNull
        Long titleId,
        @NotNull
        Long branchId,
        @NotNull
        Long departmentId,
        @NotNull
        LocalDate hireDate,
        Long managerId
) {
}
