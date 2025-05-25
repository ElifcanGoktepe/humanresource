package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;

import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.repository.CompanyBranchRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyBranchService {
    private final CompanyBranchRepository companyBranchRepository;


    public CompanyBranch addCompanyBranch(@Valid AddCompanyBranchRequestDto dto) {
        CompanyBranch companyBranch = CompanyBranch.builder()
                .companyBranchCode(dto.companyBranchCode())
                .companyBranchAddress(dto.companyBranchAddress())
                .companyBranchPhoneNumber(dto.companyBranchPhoneNumber())
                .companyBranchEmailAddress(dto.companyBranchEmailAddress())
                .build();
        return companyBranchRepository.save(companyBranch);
    }

    public CompanyBranch deleteCompanyBranch(Long id) {
        CompanyBranch companyBranch = companyBranchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company branch not found" + id));
                companyBranchRepository.delete(companyBranch);
                return companyBranch;
    }

    public List<CompanyBranch> findAll() {
        return companyBranchRepository.findAll();
    }

    public CompanyBranch findByCompanyBranchAddress(@NotNull @NotBlank @NotEmpty String companyBranchAddress) {
        if (!companyBranchRepository.existsByCompanyBranchAddress(companyBranchAddress)){
            throw new EntityNotFoundException("Company branch address not found");
        }
        return companyBranchRepository.findByCompanyBranchAddress(companyBranchAddress);
    }

    public CompanyBranch findByCompanyBranchEmailAddress(@NotNull @NotEmpty @NotBlank @Email String companyBranchEmailAddress) {
       if (!companyBranchRepository.existsByCompanyBranchEmailAddress(companyBranchEmailAddress)){
           throw new EntityNotFoundException("Company branch email address not found");
       }
       return companyBranchRepository.findByCompanyBranchEmailAddress(companyBranchEmailAddress);
    }

    public CompanyBranch findByCompanyBranchPhoneNumber(@NotNull @NotBlank @NotEmpty @Pattern(regexp = "^\\d{11}$") String companyBranchPhoneNumber) {
        if (!companyBranchRepository.existsByCompanyBranchPhoneNumber(companyBranchPhoneNumber)){
            throw new EntityNotFoundException("Company branch phone number not found");
        }
        return companyBranchRepository.findByCompanyBranchPhoneNumber(companyBranchPhoneNumber);
    }

    public CompanyBranch deleteCompanyBranchByCompanyBranchCode(String companyBranchCode) {
        CompanyBranch companyBranch = companyBranchRepository.findByCompanyBranchCode(companyBranchCode);
       if(companyBranch == null){
           throw new EntityNotFoundException("Company branch not found");
       }
        companyBranchRepository.delete(companyBranch);
         return companyBranch;
    }
}
