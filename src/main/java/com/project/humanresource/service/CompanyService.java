package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;





    public Company addCompany(@Valid AddCompanyRequestDto dto) {

        Company company = Company.builder().
                companyName(dto.companyName()).
                companyAddress(dto.companyAddress()).
                companyEmail(dto.companyEmail()).
                companyPhoneNumber(dto.companyPhoneNumber()).
                build();

        return companyRepository.save(company);
    }



    public Company findByCompanyName(String companyName) {
        if (!companyRepository.existsByCompanyName(companyName)){
            throw new EntityNotFoundException("Company not found with name: " + companyName);
        }
       return companyRepository.findByCompanyName(companyName);
    }

    public Company findByCompanyEmailAddress(@NotNull @NotEmpty @NotBlank String companyEmailAddress) {
        if(!companyRepository.existsByCompanyEmail(companyEmailAddress)){
            throw new EntityNotFoundException("Company not found with email: " + companyEmailAddress);

        }
        return companyRepository.findByCompanyEmail(companyEmailAddress);
    }

    public Company findByCompanyPhoneNumber(@NotNull @NotEmpty @NotBlank String companyPhoneNumber) {
        if (!companyRepository.existsByCompanyPhoneNumber(companyPhoneNumber)){
            throw new EntityNotFoundException("Company not found with phone number: " + companyPhoneNumber);
        }
        return companyRepository.findByCompanyPhoneNumber(companyPhoneNumber);
    }
/*
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }
 */
    public Company deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));

        companyRepository.delete(company);
        return company;
    }

    public List<Company> findAll() {

        return companyRepository.findAllByOrderByCompanyNameAsc();
    }
}
