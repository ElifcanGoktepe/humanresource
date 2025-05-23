package com.project.humanresource.entity;

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
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull @NotBlank
    String companyName;
    @NotNull @NotBlank
    String companyAddress;
    @NotNull  @NotBlank
    String companyPhoneNumber;
    @NotNull @NotBlank
    String companyEmail;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<CompanyBranch> branches = new ArrayList<>();




}


