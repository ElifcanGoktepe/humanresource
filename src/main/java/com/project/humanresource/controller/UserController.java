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
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseShort.<Employee>builder()
                .data(employee)
                .code(HttpStatus.CREATED.value())
                .message("User created successfully.")
                .build());
    }

    /**
     * Giriş işlemi
     */
    @PostMapping(LOGIN)
    public ResponseEntity<BaseResponseShort<String>> login(@RequestBody @Valid LoginRequestDto dto) {
        Optional<Employee> optionalEmployee = userService.findByEmailPassword(dto);
        if (optionalEmployee.isEmpty()) {
            throw new HumanResourceException(ErrorType.EMAIL_PASSWORD_ERROR);
        }

        Employee employee = optionalEmployee.get();

        List<String> roles = Collections.singletonList(employee.getUserRole().name());

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
                .code(HttpStatus.OK.value())
                .data(token)
                .message("You have successfully signed in.")
                .build());
    }

    /**
     * Email adresine göre kullanıcı sorgulama
     */
    @GetMapping("/by-email")
    public ResponseEntity<BaseResponse<User>> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(false, "User not found", null));
        }

        return ResponseEntity.ok(new BaseResponse<>(true, "User found", user));
    }

    /**
     * Get user profile
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<BaseResponse<UserProfileResponseDto>> getUserProfile(@PathVariable Long id, HttpServletRequest request) {
        authorizeAccess(id, request);
        Employee employee = userService.getUserProfile(id);
        
        UserProfileResponseDto profileDto = new UserProfileResponseDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getProfileImageUrl(),
                employee.getUpdatedAt(),
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
        authorizeAccess(id, request);
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
        authorizeAccess(id, request);
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
        authorizeAccess(id, request);
        String imageUrl = userService.uploadProfileImage(id, file);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(true, "Profile image uploaded successfully", imageUrl));
    }

    /**
     * Delete profile image
     */
    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<BaseResponse<String>> deleteProfileImage(@PathVariable Long id, HttpServletRequest request) {
        authorizeAccess(id, request);
        userService.deleteProfileImage(id);
        
        return ResponseEntity.ok(new BaseResponse<>(true, "Profile image deleted successfully", null));
    }

    private Long getAuthenticatedUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "User ID not found in request attributes. Ensure authentication filter is correctly setting it.");
        }
        return userId;
    }

    private void authorizeAccess(Long resourceId, HttpServletRequest request) {
        Long authenticatedUserId = getAuthenticatedUserId(request);
        if (!Objects.equals(authenticatedUserId, resourceId)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to access or modify this resource as it does not belong to you.");
        }
    }
}
