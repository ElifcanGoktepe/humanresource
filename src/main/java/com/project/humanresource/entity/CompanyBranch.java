
package com.project.humanresource.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanyBranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @NotBlank @NotEmpty
    String companyBranchCode;

    @NotNull
    @NotBlank @NotEmpty
    String companyBranchAddress;
    @NotNull
    @NotBlank @NotEmpty
    String companyBranchPhoneNumber;
    @NotNull
    @NotBlank @NotEmpty
    String companyBranchEmailAddress;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Company.class)
    @JoinColumn(name ="company_id",referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Company company;
    @Builder.Default
    @OneToMany(mappedBy = "companyBranch", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<Department> departments = new ArrayList<>();

}
