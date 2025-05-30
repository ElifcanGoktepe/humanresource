package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;

import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyBranchRepository;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyBranchService {

    private final CompanyBranchRepository companyBranchRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public CompanyBranch addCompanyBranch(@Valid AddCompanyBranchRequestDto dto) {
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        Employee manager = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + id));

        Company company = companyRepository.findByCompanyNameIgnoreCase(manager.getCompanyName())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Company not found with name: " + manager.getCompanyName()));

        CompanyBranch companyBranch = CompanyBranch.builder()
                .companyBranchCode(dto.companyBranchCode())
                .companyBranchAddress(dto.companyBranchAddress())
                .companyBranchPhoneNumber(dto.companyBranchPhoneNumber())
                .companyBranchEmailAddress(dto.companyBranchEmailAddress())
                .company(company)
                .build();

        return companyBranchRepository.save(companyBranch);
    }

    public CompanyBranch deleteCompanyBranch(Long id) {
        CompanyBranch companyBranch = companyBranchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company branch not found with id: " + id));
        companyBranchRepository.delete(companyBranch);
        return companyBranch;
    }

    public List<CompanyBranch> findAll() {
        return companyBranchRepository.findAll();
    }

    public CompanyBranch findByCompanyBranchAddress(String address) {
        if (!companyBranchRepository.existsByCompanyBranchAddress(address)) {
            throw new EntityNotFoundException("Company branch address not found");
        }
        return companyBranchRepository.findByCompanyBranchAddress(address);
    }

    public CompanyBranch findByCompanyBranchEmailAddress(String email) {
        return companyBranchRepository.findByCompanyBranchEmailAddress(email)
                .orElse(null);
    }

    public CompanyBranch findByCompanyBranchPhoneNumber(String phoneNumber) {
        if (!companyBranchRepository.existsByCompanyBranchPhoneNumber(phoneNumber)) {
            throw new EntityNotFoundException("Company branch phone number not found");
        }
        return companyBranchRepository.findByCompanyBranchPhoneNumber(phoneNumber);
    }


}