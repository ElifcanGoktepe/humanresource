
package com.project.humanresource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @NotBlank @NotEmpty
    private String departmentName;

    @NotNull @NotBlank @NotEmpty
    private String departmentCode;

    // Department bağlı olduğu Company (opsiyonel olabilir, branch varsa oraya bağlı olabilir)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    // Department bağlı olduğu Branch (opsiyonel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_branch_id")
    @JsonIgnore
    private CompanyBranch companyBranch;

}
