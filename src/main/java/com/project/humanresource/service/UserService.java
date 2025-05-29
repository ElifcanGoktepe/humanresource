package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.ChangePasswordRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.request.UpdateUserProfileRequestDto;
import com.project.humanresource.entity.Company;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
                    .companyId(dto.companyId())
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
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
     * Get user profile by employee ID
     */
    public Employee getUserProfile(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
    }
    
    /**
     * Update user profile
     */
    public Employee updateUserProfile(Long id, UpdateUserProfileRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
                
        // Update only provided fields
        if (dto.firstName() != null && !dto.firstName().trim().isEmpty()) {
            employee.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null && !dto.lastName().trim().isEmpty()) {
            employee.setLastName(dto.lastName());
        }
        if (dto.phone() != null && !dto.phone().trim().isEmpty()) {
            employee.setPhoneNumber(dto.phone());
        }
        
        return employeeRepository.save(employee);
    }
    
    /**
     * Change user password
     */
    public void changePassword(Long id, ChangePasswordRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
                
        // Verify current password (you might want to hash check here)
        if (!employee.getPassword().equals(dto.currentPassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }
        
        // Verify new password confirmation
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }
        
        // Update password (you should hash this in production)
        employee.setPassword(dto.newPassword());
        employeeRepository.save(employee);
    }
    
    /**
     * Upload profile image
     */
    public String uploadProfileImage(Long id, MultipartFile file) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
                
        try {
            // Create uploads directory if it doesn't exist
            String uploadDir = "uploads/profile-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Update employee profile image URL
            String imageUrl = "/" + uploadDir + uniqueFilename;
            employee.setProfileImageUrl(imageUrl);
            employeeRepository.save(employee);
            
            return imageUrl;
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }
    
    /**
     * Delete profile image
     */
    public void deleteProfileImage(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
                
        if (employee.getProfileImageUrl() != null) {
            try {
                // Delete file from filesystem
                Path filePath = Paths.get("." + employee.getProfileImageUrl());
                Files.deleteIfExists(filePath);
                
                // Remove URL from database
                employee.setProfileImageUrl(null);
                employeeRepository.save(employee);
                
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete profile image", e);
            }
        }
    }
}