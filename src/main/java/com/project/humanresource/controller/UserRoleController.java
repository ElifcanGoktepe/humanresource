package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddRoleRequestDto;
import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.User;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.service.UserRoleService;
import com.project.humanresource.dto.response.BaseResponse;
import com.project.humanresource.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BaseResponseShort<UserRole>> createRole(@RequestBody AddRoleRequestDto dto) {
        return ResponseEntity.ok(BaseResponseShort.<UserRole>builder()
                        .data(userRoleService.createUserRole(dto))
                        .message("User role saved successfully")
                        .code(200)
                .build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<BaseResponse<User>> getUserByEmail(@RequestParam LoginRequestDto loginRequestDto) {
        User user = userService.findByEmail(loginRequestDto.email()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(new BaseResponse<>(false, "User not found", null));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "User found", user));
    }
} 