package com.project.humanresource.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank @NotNull @NotEmpty
    String departmentName;

    @NotBlank @NotNull @NotEmpty
    String departmentDescription;




    @ManyToOne(fetch = FetchType.LAZY, targetEntity = CompanyBranch.class)
    @JoinColumn(name ="company_branch_id",referencedColumnName ="id", nullable = false)
    private CompanyBranch companyBranch;
}
