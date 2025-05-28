package com.project.humanresource.repository;

import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
@Repository  // eklendi serkan 12:24  26/05
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    List<Employee> findByCompanyId(Long companyId);     //      şirket çalışanları

    List<Employee>  findAllByTitleId(Long titleId);     //      unvana göre çalışanlar


    Optional<Employee> findByManagerId(Long managerId);

    Optional<Employee> findByEmail(String Email);

    Optional<User> findOptionalByEmailAndPassword(String email, String password);


    List<Employee> findByIsApprovedFalse();  //  26/05  pazartesi 08:19 eklendi  serkan



}
