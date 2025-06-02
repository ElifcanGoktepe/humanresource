
package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.UserRoleRepository;
import com.project.humanresource.utility.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
private final EmployeeRepository employeeRepository;
private final UserRoleRepository userRoleRepository;

    public List<Employee> getPendingManagerApplications() {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        boolean isAdmin = jwtUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("Admin"));

        if (!isAdmin) {
            throw new SecurityException("You are not authorized to perform this action");
        }


        List<UserRole> managerRoles = userRoleRepository.findAllByUserStatus(UserStatus.Manager);

        return managerRoles.stream()
                .map(role -> employeeRepository.findById(role.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(emp -> !emp.isApproved()) // Sadece onaylanmamışlar
                .toList();
    }




}
