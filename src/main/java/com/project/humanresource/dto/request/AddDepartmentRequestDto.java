package com.project.humanresource.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddDepartmentRequestDto(

        @NotBlank @NotNull @NotEmpty
        String departmentName,

        @NotBlank @NotNull @NotEmpty
                String departmentDescription

) {
}
