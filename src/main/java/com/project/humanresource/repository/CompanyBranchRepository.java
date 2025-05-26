package com.project.humanresource.repository;

import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import jakarta.validation.constraints.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CompanyBranchRepository extends JpaRepository<CompanyBranch, Long> {


    boolean existsByCompanyBranchAddress(@NotNull @NotBlank @NotEmpty String companyBranchAddress);

    CompanyBranch findByCompanyBranchAddress(@NotNull @NotBlank @NotEmpty String companyBranchAddress);

    CompanyBranch findByCompanyBranchEmailAddress(@NotNull @NotEmpty @NotBlank @Email String companyBranchEmailAddress);

    CompanyBranch findByCompanyBranchPhoneNumber(@NotNull @NotBlank @NotEmpty @Pattern(regexp = "^\\d{11}$") String companyBranchPhoneNumber);

    boolean existsByCompanyBranchPhoneNumber(@NotNull @NotBlank @NotEmpty @Pattern(regexp = "^\\d{11}$") String companyBranchPhoneNumber);

    boolean existsByCompanyBranchEmailAddress(String companyBranchEmailAddress);

    CompanyBranch findByCompanyBranchCode(String companyBranchCode);
}
