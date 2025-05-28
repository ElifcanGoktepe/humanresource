package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.ChangePasswordRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.request.UpdateUserProfileRequestDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final EmployeeRepository employeeRepository;
    private final UserRoleService userRoleService;
    private final FileUploadService fileUploadService;


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
                    .companyId(dto.companyId())
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

    /**
     * Update user profile information
     */
    @Transactional
    public Employee updateUserProfile(Long id, @Valid UpdateUserProfileRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
        
        // Check if email is being changed and if it's already in use
        if (!employee.getEmailWork().equals(dto.email())) {
            Optional<Employee> existingEmployee = employeeRepository.findByEmailWork(dto.email());
            if (existingEmployee.isPresent()) {
                throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
            }
        }
        
        employee.setFirstName(dto.firstName());
        employee.setLastName(dto.lastName());
        employee.setEmailWork(dto.email());
        employee.setPhoneWork(dto.phone());
        
        return employeeRepository.save(employee);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long id, @Valid ChangePasswordRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
        
        // TODO: In production, use BCrypt or similar for password hashing
        // For now, using plain text comparison (NOT SECURE)
        if (!employee.getPassword().equals(dto.currentPassword())) {
            throw new HumanResourceException(ErrorType.CURRENT_PASSWORD_INCORRECT);
        }
        
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }
        
        // TODO: Hash the new password before saving in production
        employee.setPassword(dto.newPassword());
        employeeRepository.save(employee);
    }

    /**
     * Get user profile information
     */
    public Employee getUserProfile(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
    }

    /**
     * Upload profile image
     */
    @Transactional
    public String uploadProfileImage(Long id, MultipartFile file) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
        
        // Eski profil fotoğrafını sil
        if (employee.getProfileImageUrl() != null) {
            fileUploadService.deleteProfileImage(employee.getProfileImageUrl());
        }
        
        // Yeni profil fotoğrafını yükle
        String imageUrl = fileUploadService.uploadProfileImage(file, id);
        
        // Database'i güncelle
        employee.setProfileImageUrl(imageUrl);
        employeeRepository.save(employee);
        
        return imageUrl;
    }

    /**
     * Delete profile image
     */
    @Transactional
    public void deleteProfileImage(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
        
        if (employee.getProfileImageUrl() != null) {
            fileUploadService.deleteProfileImage(employee.getProfileImageUrl());
            employee.setProfileImageUrl(null);
            employeeRepository.save(employee);
        }
    }
}