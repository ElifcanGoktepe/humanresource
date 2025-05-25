package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddDepartmentRequestDto;
import com.project.humanresource.entity.Department;
import com.project.humanresource.repository.DepartmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public Department addDepartment(@Valid AddDepartmentRequestDto dto) {
        Department department=Department.builder()
                .departmentName(dto.departmentName())
                .departmentDescription(dto.departmentDescription())
                .build();
        return departmentRepository.save(department);

    }

}
