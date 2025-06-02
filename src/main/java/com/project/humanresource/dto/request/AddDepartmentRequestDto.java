
package com.project.humanresource.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddDepartmentRequestDto(
        Long id,
        @NotNull @NotBlank @NotEmpty
        String departmentName,

        @NotNull @NotBlank @NotEmpty
        String departmentCode,

        // Şirket id - Department e doğrudan bağlıysa
        Long companyId,

        // Branch id - Department e branch üzerinden bağlıysa
        Long companyBranchId

) {
}
