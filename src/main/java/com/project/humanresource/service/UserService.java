package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.ChangePasswordRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.request.UpdateUserProfileRequestDto;
import com.project.humanresource.dto.response.UserProfileResponseDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Employee createUser(@Valid AddUserRequestDto dto) {

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

        return employeeRepository.save(employee);
    }

    public Optional<User> findByEmailWorkPassword(@Valid LoginRequestDto dto) {
        return employeeRepository.findOptionalByEmailWorkAndPassword(dto.email(), dto.password());
    }

    public UserProfileResponseDto getUserProfile(Long userId) {
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + userId));
        
        return new UserProfileResponseDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneWork(),
                null, // profile image URL not implemented yet
                LocalDateTime.now(), // using current time as placeholder
                employee.isActive()
        );
    }

    public UserProfileResponseDto updateUserProfile(Long userId, @Valid UpdateUserProfileRequestDto dto) {
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + userId));
        
        employee.setFirstName(dto.firstName());
        employee.setLastName(dto.lastName());
        employee.setEmail(dto.email());
        employee.setPhoneWork(dto.phone());
        
        Employee updated = employeeRepository.save(employee);
        
        return new UserProfileResponseDto(
                updated.getId(),
                updated.getFirstName(),
                updated.getLastName(),
                updated.getEmail(),
                updated.getPhoneWork(),
                null, // profile image URL not implemented yet
                LocalDateTime.now(), // using current time as placeholder
                updated.isActive()
        );
    }

    public boolean changePassword(Long userId, @Valid ChangePasswordRequestDto dto) {
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + userId));
        
        // TODO: CRITICAL SECURITY ISSUE - Passwords should be hashed with BCrypt or similar
        // Current implementation stores passwords in plain text which is a major security vulnerability
        
        // Validate current password
        if (!employee.getPassword().equals(dto.currentPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Validate new password confirmation
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }
        
        // TODO: Hash the new password before saving
        employee.setPassword(dto.newPassword());
        employeeRepository.save(employee);
        
        return true;
    }
} 