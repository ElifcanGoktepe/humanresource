package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
// import com.project.humanresource.entity.CompanyBranch; // Kullanılmıyor gibi görünüyor, gerekirse eklenir.
import com.project.humanresource.entity.Employee;
// import com.project.humanresource.entity.UserRole; // UserRole entity'si kaldırıldı.
import com.project.humanresource.repository.EmployeeRepository;
// import com.project.humanresource.repository.UserRoleRepository; // UserRoleRepository kaldırıldı.
import com.project.humanresource.utility.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
// import java.util.Optional; // Kullanılmıyor gibi görünüyor.

@Service
@RequiredArgsConstructor
public class AdminService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> getPendingManagerApplications() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JwtUser jwtUser;

        if (principal instanceof JwtUser) {
            jwtUser = (JwtUser) principal;
        } else {
            // Bu durum genellikle anonim kullanıcılar veya beklenmedik principal türleri için oluşur.
            // Projenizin güvenlik yapılandırmasına göre uygun bir exception fırlatılabilir.
            throw new SecurityException("User details not found in security context or not of expected type JwtUser.");
        }

        boolean isAdmin = jwtUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("Admin") || role.equals("ROLE_Admin")); // Farklı prefixler olabilir

        if (!isAdmin) {
            throw new SecurityException("You are not authorized to perform this action. Required role: Admin.");
        }

        // Find employees who are managers and are not yet approved.
        // Employee entity'sinde userRole ve isApproved alanları olmalı.
        return employeeRepository.findByUserRoleAndIsApproved(UserStatus.Manager, false);
    }
}
