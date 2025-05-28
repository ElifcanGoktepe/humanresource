package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddDepartmentRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Department;
import com.project.humanresource.repository.CompanyBranchRepository;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;

    public Department addDepartment(@Valid AddDepartmentRequestDto dto) {

        Department department = Department.builder()
                .departmentName(dto.departmentName())
                .departmentCode(dto.departmentCode())
                .build();

        // Company id varsa ata
        if(dto.companyId() != null){
            Company company = companyRepository.findById(dto.companyId())
                    .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + dto.companyId()));
            department.setCompany(company);
        }

        // Branch id varsa ata
        if(dto.companyBranchId() != null){
            CompanyBranch branch = companyBranchRepository.findById(dto.companyBranchId())
                    .orElseThrow(() -> new EntityNotFoundException("Company branch not found with id: " + dto.companyBranchId()));
            department.setCompanyBranch(branch);
        }

        return departmentRepository.save(department);
    }

    public Department findById(Long id){
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
    }

    public List<Department> findAll(){
        return departmentRepository.findAll();
    }

    public Department deleteDepartment(Long id){
        Department department = findById(id);
        departmentRepository.delete(department);
        return department;
    }

    public Department findByDepartmentCode(String code){
        Department department = departmentRepository.findByDepartmentCode(code);
        if(department == null){
            throw new EntityNotFoundException("Department not found with code: " + code);
        }
        return department;
    }
}
