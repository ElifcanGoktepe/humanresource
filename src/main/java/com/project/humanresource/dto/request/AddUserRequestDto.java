package com.project.humanresource.dto.request;

import com.project.humanresource.utility.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddUserRequestDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,
        
        @NotBlank(message = "Password confirmation is required")
        String rePassword,
        
        // Optional fields
        String firstName,
        String lastName,
        String phone,
        Long companyId,
        Long titleId,
        Long personalFiledId,

         UserStatus role
) {}