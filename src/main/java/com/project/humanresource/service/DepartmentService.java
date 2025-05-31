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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;

    public Department addDepartment(AddDepartmentRequestDto dto) {

        Department department = Department.builder()
                .departmentName(dto.departmentName())
                .departmentCode(dto.departmentCode())
                .build();

        // Eğer companyId varsa entity set et
        if (dto.companyId() != null) {
            Company company = companyRepository.findById(dto.companyId())
                    .orElseThrow(() -> new RuntimeException("Company not found with id " + dto.companyId()));
            department.setCompany(company);
        }

        // Eğer companyBranchId varsa entity set et
        if (dto.companyBranchId() != null) {
            CompanyBranch branch = companyBranchRepository.findById(dto.companyBranchId())
                    .orElseThrow(() -> new RuntimeException("CompanyBranch not found with id " + dto.companyBranchId()));
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

    public List<AddDepartmentRequestDto> getDepartmentsByBranchId(Long branchId) {
        return departmentRepository.findByCompanyBranchId(branchId)
                .stream()
                .map(d -> new AddDepartmentRequestDto(
                        d.getDepartmentName(),
                        d.getDepartmentCode(),
                        d.getCompany() != null ? d.getCompany().getId() : null,
                        d.getCompanyBranch() != null ? d.getCompanyBranch().getId() : null
                ))
                .toList();
    }

}



