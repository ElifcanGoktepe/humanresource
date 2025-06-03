package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
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
    private final EmployeeRepository employeeRepository;

    public Optional<User> findByEmailAndPassword(String email  ,String password) {
        return userRepository.findOptionalByEmailAndPassword(email, password);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Employee createUser(@Valid AddUserRequestDto dto) {
        if (dto.role() == UserStatus.Admin) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof JwtUser)) {
                throw new HumanResourceException(
                    ErrorType.UNAUTHORIZED,
                    "User not authenticated or principal is not JwtUser."
                );
            }
            JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
            boolean isAdminCreator = jwtUser.getAuthorities().stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .anyMatch(role -> role.equals("Admin") || role.equals("ROLE_Admin"));

            if (!isAdminCreator) {
                throw new HumanResourceException(
                    ErrorType.UNAUTHORIZED,
                    "You are not authorized to create Admin users."
                );
            }
            
            if (employeeRepository.findByEmail(dto.email()).isPresent()) {
                throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
            }

            Employee admin = Employee.builder()
                    .email(dto.email())
                    .password(dto.password()) 
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .userRole(UserStatus.Admin) 
                    .isActive(true)
                    .isApproved(true)
                    .isActivated(true)
                    .build();

            return employeeRepository.save(admin);

        } else if (dto.role() == UserStatus.Manager || dto.role() == UserStatus.Employee) {
            Long managerIdFromToken = null;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof JwtUser) {
                managerIdFromToken = ((JwtUser) principal).getUserId(); 
            }
            if (managerIdFromToken == null && dto.role() == UserStatus.Employee) { 
                 throw new HumanResourceException(ErrorType.INVALID_TOKEN, "Manager ID could not be retrieved from token for Employee creation.");
            }

            if (employeeRepository.findByEmail(dto.email()).isPresent()) {
                throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
            }

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
                    .managerId(dto.role() == UserStatus.Manager ? null : managerIdFromToken) 
                    .userRole(dto.role()) 
                    .build();

            return employeeRepository.save(employee);

        } else {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Invalid user role: " + dto.role());
        }
    }

    public Optional<Employee> findByEmailPassword(@Valid LoginRequestDto dto) {
        return employeeRepository.findOptionalByEmailAndPassword(dto.email(), dto.password());
    }

    public Optional<User> findByEmail(@Email @NotEmpty @NotNull String email) {
        return userRepository.findByEmail(email);
    }
    
    public Employee getUserProfile(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
    }
    
    public Employee updateUserProfile(Long id, UpdateUserProfileRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
                
        if (dto.firstName() != null && !dto.firstName().trim().isEmpty()) {
            employee.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null && !dto.lastName().trim().isEmpty()) {
            employee.setLastName(dto.lastName());
        }
        if (dto.phone() != null && !dto.phone().trim().isEmpty()) {
            employee.setPhoneNumber(dto.phone());
        }

        if (dto.email() != null && !dto.email().trim().isEmpty() && !employee.getEmail().equalsIgnoreCase(dto.email())) {
            Optional<Employee> existingEmployeeWithNewEmail = employeeRepository.findByEmail(dto.email());
            if (existingEmployeeWithNewEmail.isPresent() && !existingEmployeeWithNewEmail.get().getId().equals(employee.getId())) {
                throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
            }
            employee.setEmail(dto.email());
        }
        
        return employeeRepository.save(employee);
    }
    
    public void changePassword(Long id, ChangePasswordRequestDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));

        if (!employee.getPassword().equals(dto.currentPassword())) {
            throw new HumanResourceException(ErrorType.CURRENT_PASSWORD_INCORRECT);
        }
        
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }
        
        employee.setPassword(dto.newPassword());
        employeeRepository.save(employee);
    }
    
    public String uploadProfileImage(Long id, MultipartFile file) {
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
            throw new HumanResourceException(ErrorType.INVALID_FILE_NAME, "Filename must include an extension.");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new HumanResourceException(ErrorType.INVALID_FILE_EXTENSION, "Invalid file extension: " + extension);
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));

        try {
            String uploadDir = "uploads/profile-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String imageUrl = "/" + uploadDir + uniqueFilename; 
            employee.setProfileImageUrl(imageUrl);
            employeeRepository.save(employee);
            return imageUrl;
        } catch (IOException e) {
            throw new HumanResourceException(
                ErrorType.FILE_UPLOAD_ERROR, 
                "Failed to save the uploaded profile image due to a server I/O error."
            );
        }
    }
    
    public void deleteProfileImage(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
                
        if (employee.getProfileImageUrl() != null) {
            try {
                Path filePath = Paths.get("." + employee.getProfileImageUrl()); 
                Files.deleteIfExists(filePath);
                employee.setProfileImageUrl(null);
                employeeRepository.save(employee);
            } catch (IOException e) {
                throw new HumanResourceException(
                    ErrorType.INTERNAL_SERVER, 
                    "Failed to delete the profile image due to a server I/O error."
                );
            }
        }
    }
}