
package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyBranchRepository;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
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


    public CompanyBranch addCompanyBranch(AddCompanyBranchRequestDto dto) {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userId));

        Company company = companyRepository.findByCompanyNameIgnoreCase(manager.getCompanyName())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with name: " + manager.getCompanyName()));

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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company branch not found with id: " + id));


        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();


        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userId));
        String managerCompanyName = manager.getCompanyName();


        String branchCompanyName = companyBranch.getCompany().getCompanyName();


        if (!managerCompanyName.equalsIgnoreCase(branchCompanyName)) {
            throw new RuntimeException("Unauthorized to delete this branch");
        }


        companyBranchRepository.delete(companyBranch);
        return companyBranch;
    }


    public List<CompanyBranch> findAll() {
        return companyBranchRepository.findAll();
    }


    public List<CompanyBranch> findAllCompanyBranchesOfSelectedCompany(Long companyId) {
        // companyId, Controller'dan geliyor

        List<CompanyBranch> branches = companyBranchRepository.findByCompanyId(companyId);

        if (branches.isEmpty()) {
            throw new RuntimeException("Company branches not found with company id: " + companyId);
        }

        return branches;
    }




    public List<CompanyBranch> findAllCompanyBranches() {
        return companyBranchRepository.findAll();
    }

}
