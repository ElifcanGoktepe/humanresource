
package com.project.humanresource.repository;

import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import jakarta.validation.constraints.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyBranchRepository extends JpaRepository<CompanyBranch, Long> {


    List<CompanyBranch> findByCompanyBranchCodeIgnoreCase(String companyBranchCode);

    List<CompanyBranch> findByCompanyId(Long companyId);

}
