package com.project.humanresource.repository;

import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import jakarta.validation.constraints.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyBranchRepository extends JpaRepository<CompanyBranch, Long> {



    boolean existsByCompanyBranchAddress(String address);

    CompanyBranch findByCompanyBranchAddress(String address);

    Optional<CompanyBranch> findByCompanyBranchEmailAddress(String email);

    boolean existsByCompanyBranchPhoneNumber(String phoneNumber);

    CompanyBranch findByCompanyBranchPhoneNumber(String phoneNumber);

    CompanyBranch findByCompanyBranchCode(String code);

}
