package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddEmployeeForRoleRequirementDto;
import com.project.humanresource.dto.request.AddEmployeeRequestDto;
import com.project.humanresource.dto.request.SetPersonelFileRequestDto;
import com.project.humanresource.entity.*;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.*;
import com.project.humanresource.utility.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationService emailVerificationService;

    public Optional<Employee> findById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public void addEmployeeForManager(AddEmployeeForRoleRequirementDto dto, HttpServletRequest request) { //manager tarafından eklenen employee

        Long managerId = (Long) request.getAttribute("userId");

        if (managerId == null) {
            throw new IllegalStateException("Manager ID not found in request.");
        }
        Employee employee = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .emailWork(dto.emailWork())
                .phoneWork(dto.phoneWork())
                .companyName(dto.companyName())
                .titleName(dto.titleName())
                .isApproved(true)
                .managerId(managerId)
                .build();
        employeeRepository.save(employee);

        UserRole employeeRole = UserRole.builder()
                .userStatus(UserStatus.Employee)
                .userId(employee.getId())
                .build();
        userRoleRepository.save(employeeRole);

        emailVerificationService.sendVerificationEmail(employee.getEmailWork());

    }

}