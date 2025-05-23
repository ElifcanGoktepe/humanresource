package com.project.humanresource.repository;

import com.project.humanresource.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    List<Employee> findByCompanyId(Long companyId);     //      şirket çalışanları

    List<Employee>  findAllByTitleId(Long titleId);     //      unvana göre çalışanlar


    Optional<Employee> findByUserId(Long userId);

    Optional<Employee> findByEmailWork(String toEmail);
}
