package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.ChangePasswordRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.request.UpdateUserProfileRequestDto;
import com.project.humanresource.dto.response.BaseResponse;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.UserProfileResponseDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.service.UserRoleService;
import com.project.humanresource.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.project.humanresource.config.RestApis.CREATEUSER;
import static com.project.humanresource.config.RestApis.LOGIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final JwtManager jwtManager;
    private final UserRoleService userRoleService;
    private final EmployeeRepository employeeRepository;

    /**
     * Kullanıcı oluşturma
     */
    @PostMapping(CREATEUSER)
    public ResponseEntity<BaseResponseShort<Employee>> createUser(@RequestBody @Valid AddUserRequestDto dto) {
        if (!dto.password().equals(dto.rePassword())) {
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        }

        Employee employee = userService.createUser(dto);

        return ResponseEntity.ok(BaseResponseShort.<Employee>builder()
                .data(employee)
                .code(200)
                .message("User created successfully.")
                .build());
    }

    /**
     * Giriş işlemi
     */
    @PostMapping(LOGIN)
    public ResponseEntity<BaseResponseShort<String>> login(@RequestBody @Valid LoginRequestDto dto) {
        Optional<Employee> optionalEmployee = userService.findByEmailPassword(dto);
        System.out.println("optionalEmployee present? " + optionalEmployee.isPresent());
        if (optionalEmployee.isEmpty()) {
            System.out.println("DEBUG - User not found, throwing exception");
            throw new HumanResourceException(ErrorType.EMAIL_PASSWORD_ERROR);
        }

        Employee employee = optionalEmployee.get();

        // Roller çekiliyor
        List<UserRole> userRoles = userRoleService.findAllByUserId(employee.getId());
        List<String> roles = userRoles.stream()
                .map(role -> role.getUserStatus().name())
                .toList();

        // Token oluştur
        String token = jwtManager.createToken(
                employee.getEmail(),
                employee.getId(),
                roles,
                employee.getFirstName(),
                employee.getLastName(),
                employee.getTitleName(),
                employee.getCompanyName()
        );

        return ResponseEntity.ok(BaseResponseShort.<String>builder()
                .code(200)
                .data(token)
                .message("You have successfully signed in.")
                .build());
    }

    /**
     * Email adresine göre kullanıcı sorgulama
     */
    @GetMapping("/by-email")
    public ResponseEntity<BaseResponse<User>> getUserByEmail(@RequestParam LoginRequestDto loginRequestDto) {
        User user = userService.findByEmail(loginRequestDto.email()).orElse(null);

        if (user == null) {
            return ResponseEntity.ok(new BaseResponse<>(false, "User not found", null));
        }

        return ResponseEntity.ok(new BaseResponse<>(true, "User found", user));
    }

    /**
     * Get user profile
     */
   @GetMapping("/{id}/profile")
    public ResponseEntity<BaseResponse<UserProfileResponseDto>> getUserProfile(@PathVariable Long id, HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        if (!Objects.equals(authenticatedUserId, id)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to access or modify this resource as it does not belong to you.");
        }
        Employee employee = userService.getUserProfile(id);
        
        UserProfileResponseDto profileDto = new UserProfileResponseDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getProfileImageUrl(),
                null, // lastUpdated - implement later
                employee.isActive()
        );
        
        return ResponseEntity.ok(new BaseResponse<>(true, "Profile retrieved successfully", profileDto));
    }

    /**
     * Update user profile
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<BaseResponse<Employee>> updateUserProfile(
            @PathVariable Long id, 
            @RequestBody @Valid UpdateUserProfileRequestDto dto, HttpServletRequest request) {
        
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        if (!Objects.equals(authenticatedUserId, id)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to access or modify this resource as it does not belong to you.");
        }
        Employee updatedEmployee = userService.updateUserProfile(id, dto);
        
        return ResponseEntity.ok(new BaseResponse<>(true, "Profile updated successfully", updatedEmployee));
    }

    /**
     * Change user password
     */
   @PutMapping("/{id}/password")
    public ResponseEntity<BaseResponse<String>> changePassword(
            @PathVariable Long id, 
            @RequestBody @Valid ChangePasswordRequestDto dto, HttpServletRequest request) {
        
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        if (!Objects.equals(authenticatedUserId, id)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to access or modify this resource as it does not belong to you.");
        }
        userService.changePassword(id, dto);
        
        return ResponseEntity.ok(new BaseResponse<>(true, "Password changed successfully", null));
    }

    /**
     * Upload profile image
     */
   @PostMapping("/{id}/profile-image")
    public ResponseEntity<BaseResponse<String>> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        if (!Objects.equals(authenticatedUserId, id)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to access or modify this resource as it does not belong to you.");
        }
        String imageUrl = userService.uploadProfileImage(id, file);
        
        return ResponseEntity.ok(new BaseResponse<>(true, "Profile image uploaded successfully", imageUrl));
    }

    /**
     * Delete profile image
     */
    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<BaseResponse<String>> deleteProfileImage(@PathVariable Long id, HttpServletRequest request) {
        
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        if (!Objects.equals(authenticatedUserId, id)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to access or modify this resource as it does not belong to you.");
        }
        userService.deleteProfileImage(id);
        
        return ResponseEntity.ok(new BaseResponse<>(true, "Profile image deleted successfully", null));
    }

    @GetMapping("/{employeeId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        List<UserRole> userRoles = userRoleService.findAllByUserId(employee.getId());
        List<String> roleNames = userRoles.stream()
                .map(userRole -> userRole.getUserStatus().name())
                .toList();

        return ResponseEntity.ok(roleNames);
    }
}
