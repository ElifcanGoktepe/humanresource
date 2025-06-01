package com.project.humanresource.repository;

import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
 // eklendi serkan 12:24  26/05
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    List<Employee> findByCompanyId(Long companyId);     //      şirket çalışanları

    List<Employee>  findAllByTitleId(Long titleId);     //      unvana göre çalışanlar


    Optional<Employee> findByManagerId(Long managerId);

    Optional<Employee> findByEmail(String Email);

    Optional<Employee> findOptionalByEmailAndPassword(String email, String password);

    @Query("SELECT e.id FROM Employee e WHERE e.managerId = :managerId")
    List<Long> findEmployeeIdsByManagerId(@Param("managerId") Long managerId);



}
