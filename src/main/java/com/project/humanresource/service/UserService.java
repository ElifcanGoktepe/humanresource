package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.UserRepository;
import com.project.humanresource.utility.UserStatus;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final EmployeeRepository employeeRepository;
    private final UserRoleService userRoleService;


    public Optional<User> findByEmailAndPassword(String email  ,String password) {
        return userRepository.findOptionalByEmailAndPassword(email , password);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Employee createUser(@Valid AddUserRequestDto dto) {
        Long managerId = (Long) request.getAttribute("userId");

        if (dto.role() == UserStatus.Admin) {
            Employee admin = Employee.builder()
                    .email(dto.email()) // ya da email
                    .password(dto.password()) // TODO: şifre hashle
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .userRole(UserStatus.Admin)
                    .isActive(true)
                    .isApproved(true)
                    .isActivated(true)
                    .build();

            Employee savedAdmin = employeeRepository.save(admin);
            userRoleService.save(UserStatus.Admin, savedAdmin.getId());
            return savedAdmin;

        } else if (dto.role() == UserStatus.Manager || dto.role() == UserStatus.Employee) {
            Employee employee = Employee.builder()
                    .email(dto.email())
                    .password(dto.password())
                    .isActive(true)
                    .isApproved(false)
                    .isActivated(false)
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .company(Company.builder().id(dto.companyId()).build())
                    .titleId(dto.titleId())
                    .personalFiledId(dto.personalFiledId())
                    .managerId(managerId)
                    .userRole(dto.role())
                    .build();

            Employee savedEmployee = employeeRepository.save(employee);
            userRoleService.save(dto.role(), savedEmployee.getId());
            return savedEmployee;

        } else {
            throw new RuntimeException("Invalid user role: " + dto.role());
        }
    }

    private void save(UserStatus userStatus, Long id) {
    }


    public Optional<User> findByEmailPassword(@Valid LoginRequestDto dto) {
        return employeeRepository.findOptionalByEmailAndPassword(dto.email(), dto.password());
    }

    public Optional<User> findByEmail(@Email @NotEmpty @NotNull String email) {
        return userRepository.findByEmail(email);
    }
}