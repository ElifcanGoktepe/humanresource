package com.project.humanresource.repository;

import com.project.humanresource.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Department findByDepartmentCode(String departmentCode);

    boolean existsByDepartmentCode(String departmentCode);

    List<Department> findByCompanyBranchId(Long companyBranchId);

}
