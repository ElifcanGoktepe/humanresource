package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final EmployeeRepository employeeRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(@Valid AddUserRequestDto dto) {

        Long managerId = (Long) request.getAttribute("userId");

        Employee employee = Employee.builder()
                .emailWork(dto.emailWork())
                .password(dto.password())
                .isActive(true)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .companyId(dto.companyId())
                .titleId(dto.titleId())
                .personalFiledId(dto.personalFiledId())
                .managerId(managerId)
                .build();

        return userRepository.save(employee);
    }

    public Optional<User> findByEmailWorkPassword(@Valid LoginRequestDto dto) {
        return employeeRepository.findOptionalByEmailWorkAndPassword(dto.email(), dto.password());
    }
}