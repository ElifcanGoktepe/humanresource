package com.project.humanresource.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters")
        String newPassword,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
} 