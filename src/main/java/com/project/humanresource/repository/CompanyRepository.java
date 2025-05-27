package com.project.humanresource.repository;

import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.entity.Company;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository <Company , Long> {



    Company findByCompanyName(String companyName);

    Company findByCompanyEmail(String EmailAddress);

    Company findByCompanyPhoneNumber(String PhoneNumber);

    List<Company> findAllByOrderByCompanyNameAsc();


    boolean existsByCompanyName(String companyName);

    boolean existsByCompanyEmail(String companyEmail);

    boolean existsByCompanyPhoneNumber(String companyPhoneNumber);
}
