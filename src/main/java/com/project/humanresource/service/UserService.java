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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

    private final UserRepository userRepository;
    private final HttpServletRequest request;
    private final EmployeeRepository employeeRepository;
    private final UserRoleService userRoleService;

    public Optional<User> findByEmailAndPassword(String email  ,String password) {
        return userRepository.findOptionalByEmailAndPassword(email, password);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Employee createUser(@Valid AddUserRequestDto dto) {
        Long managerId = (Long) request.getAttribute("userId");

        // Authorization check: Only Admins can create other Admins
        if (dto.role() == UserStatus.Admin) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new HumanResourceException(
                    ErrorType.UNAUTHORIZED,
                    "User not authenticated." // Should ideally not happen if Spring Security is configured correctly
                );
            }
            java.util.Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAdminCreator = authorities.contains(new SimpleGrantedAuthority("Admin"));

            if (!isAdminCreator) {
                throw new HumanResourceException(
                    ErrorType.UNAUTHORIZED,
                    "You are not authorized to create Admin users."
                );
            }

            // Existing Admin creation logic
            Employee admin = Employee.builder()
                    .email(dto.email()) // ya da email
                    .password(dto.password())
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

    public Optional<Employee> findByEmailPassword(@Valid LoginRequestDto dto) {
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

        // Update email if provided and different, with uniqueness check
        if (dto.email() != null && !dto.email().trim().isEmpty() && !employee.getEmail().equalsIgnoreCase(dto.email())) {
            Optional<Employee> existingEmployeeWithNewEmail = employeeRepository.findByEmail(dto.email());
            if (existingEmployeeWithNewEmail.isPresent() && !existingEmployeeWithNewEmail.get().getId().equals(employee.getId())) {
                // If the email exists and it's not for the current employee, throw error
                throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
            }
            employee.setEmail(dto.email());
            // Consider email re-verification logic here in a real-world scenario
            // For example, set employee.setIsActivated(false) and trigger verification email
        }
        
        return employeeRepository.save(employee);
    }
    
    /**
     * Change user password
     */
    public void changePassword(Long id, ChangePasswordRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));

        // Verify current password (plain text comparison)
        if (!employee.getPassword().equals(dto.currentPassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }
        
        // Verify new password confirmation
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }
        
        // Update password (plain text)
        employee.setPassword(dto.newPassword());
        employeeRepository.save(employee);
    }
    
    /**
     * Upload profile image
     */
    public String uploadProfileImage(Long id, MultipartFile file) {
        // --- File Validation Start ---
        if (file.isEmpty()) {
            throw new HumanResourceException(ErrorType.FILE_NOT_FOUND, "Uploaded file is empty.");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new HumanResourceException(ErrorType.FILE_SIZE_TOO_LARGE);
        }

        if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
            throw new HumanResourceException(ErrorType.INVALID_FILE_TYPE, "Invalid MIME type: " + file.getContentType());
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf('.') == -1) {
            // No extension or invalid filename
            throw new HumanResourceException(ErrorType.INVALID_FILE_NAME, "Filename must include an extension.");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new HumanResourceException(ErrorType.INVALID_FILE_EXTENSION, "Invalid file extension: " + extension);
        }
        // --- File Validation End ---

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
            // Log the original exception if logging is available: log.error("Error uploading profile image for user {}: {}", id, e.getMessage(), e);
            throw new HumanResourceException(
                ErrorType.FILE_UPLOAD_ERROR, 
                "Failed to save the uploaded profile image due to a server I/O error."
            );
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
                // Log the original exception if logging is available: log.error("Error deleting profile image for user {}: {}", id, e.getMessage(), e);
                throw new HumanResourceException(
                    ErrorType.INTERNAL_SERVER, // Using INTERNAL_SERVER as a generic for unexpected file system issues during delete
                    "Failed to delete the profile image due to a server I/O error."
                );
            }
        }
    }
}