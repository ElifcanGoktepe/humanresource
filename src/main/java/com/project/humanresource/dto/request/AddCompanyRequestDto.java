package com.project.humanresource.dto.request;

import jakarta.validation.constraints.*;

public record AddCompanyRequestDto(


        @NotNull @NotEmpty @NotBlank
                String companyName,
        @NotNull @NotEmpty @NotBlank
                String companyAddress,
        @NotNull @NotEmpty @NotBlank @Pattern(regexp = "^\\d{11}$")
                String companyPhoneNumber,
        @NotNull @NotEmpty @NotBlank @Email
                String companyEmail

) {
}
