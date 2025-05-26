package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.ChangePasswordRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.request.UpdateUserProfileRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.UserProfileResponseDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.service.UserRoleService;
import com.project.humanresource.service.UserService;
import com.project.humanresource.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.project.humanresource.config.RestApis.CREATEUSER;
import static com.project.humanresource.config.RestApis.LOGIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final JwtManager jwtManager;
    private final UserRoleService userRoleService;

    @PostMapping(CREATEUSER)
    public ResponseEntity<BaseResponseShort<Employee>> createUser(@RequestBody @Valid AddUserRequestDto dto) {
        if(!dto.password().equals(dto.rePassword()))
            throw new HumanResourceException(ErrorType.PASSWORD_MISMATCH);
        return ResponseEntity.ok(BaseResponseShort.<Employee>builder()
                        .data(userService.createUser(dto))
                        .code(200)
                        .message("Employee created successfully.")
                .build());
    }

    @PostMapping(LOGIN)
    public ResponseEntity<BaseResponseShort<String>> login(@RequestBody @Valid LoginRequestDto dto){
        Optional<User> optionalUser = userService.findByEmailWorkPassword(dto);
        if(optionalUser.isEmpty())
            throw new HumanResourceException(ErrorType.EMAIL_PASSWORD_ERROR);

        User user = optionalUser.get();

        // Employee aktif/pasif durumu kontrolü
        if (user instanceof Employee) {
            Employee employee = (Employee) user;
            if (!employee.isActive()) {
                throw new HumanResourceException(ErrorType.UNAUTHORIZED);
            }
            if (!employee.isActivated()) {
                return ResponseEntity.ok(BaseResponseShort.<String>builder()
                        .code(403)
                        .data(null)
                        .message("Your account is not activated. Please verify your email.")
                        .build());
            }
            if (!employee.isApproved()) {
                return ResponseEntity.ok(BaseResponseShort.<String>builder()
                        .code(403)
                        .data(null)
                        .message("Your account is waiting for admin approval.")
                        .build());
            }
        }

        // ⬇️ rollerini çek
        List<UserRole> userRoles = userRoleService.findAllRole(user.getId());
        List<String> roles = userRoles.stream()
                .map(role -> role.getUserStatus().name())
                .toList();

        String token = jwtManager.createToken(user.getId(), roles);

        return ResponseEntity.ok(BaseResponseShort.<String>builder()
                .code(200)
                .data(token)
                .message("You have successfully signed in.")
                .build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<BaseResponse<User>> getUserByEmail(@RequestParam(name = "email") String email) {
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(new BaseResponse<>(false, "User not found", null));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "User found", user));
    }

    // ========== USER SETTINGS ENDPOINTS ==========

    @GetMapping("/{userId}/profile")
    public ResponseEntity<BaseResponse<UserProfileResponseDto>> getUserProfile(@PathVariable Long userId) {
        try {
            // Authentication check - kullanıcı sadece kendi profilini görebilir
            String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            if (currentEmail == null || currentEmail.equals("anonymousUser")) {
                return ResponseEntity.ok(new BaseResponse<>(false, "Authentication required", null));
            }
            
            UserProfileResponseDto profile = userService.getUserProfile(userId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Profile retrieved successfully", profile));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new BaseResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<BaseResponse<UserProfileResponseDto>> updateUserProfile(
            @PathVariable Long userId, 
            @Valid @RequestBody UpdateUserProfileRequestDto dto) {
        try {
            UserProfileResponseDto updatedProfile = userService.updateUserProfile(userId, dto);
            return ResponseEntity.ok(new BaseResponse<>(true, "Profile updated successfully", updatedProfile));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new BaseResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<BaseResponse<String>> changePassword(
            @PathVariable Long userId, 
            @Valid @RequestBody ChangePasswordRequestDto dto) {
        try {
            // Authentication check - kullanıcı sadece kendi şifresini değiştirebilir
            String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            if (currentEmail == null || currentEmail.equals("anonymousUser")) {
                return ResponseEntity.ok(new BaseResponse<>(false, "Authentication required", null));
            }
            
            boolean success = userService.changePassword(userId, dto);
            if (success) {
                return ResponseEntity.ok(new BaseResponse<>(true, "Password changed successfully", null));
            } else {
                return ResponseEntity.ok(new BaseResponse<>(false, "Failed to change password", null));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new BaseResponse<>(false, e.getMessage(), null));
        }
    }
} 