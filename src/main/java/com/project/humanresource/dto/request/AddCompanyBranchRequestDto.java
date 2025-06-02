
package com.project.humanresource.dto.request;

import jakarta.validation.constraints.*;

public record AddCompanyBranchRequestDto(



        @NotNull
        @NotBlank @NotEmpty
        String companyBranchCode,
        @NotNull
        @NotBlank @NotEmpty
        String companyBranchAddress,
        @NotNull
        @NotBlank @NotEmpty @Pattern(regexp = "^\\d{11}$")
        String companyBranchPhoneNumber,
        @NotNull @NotEmpty
        @NotBlank @Email
        String companyBranchEmailAddress
) {
}
