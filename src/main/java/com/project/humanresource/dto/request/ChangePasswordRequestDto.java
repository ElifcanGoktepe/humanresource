package com.project.humanresource.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(
        @NotBlank(message = "Mevcut şifre zorunludur")
        String currentPassword,
        
        @NotBlank(message = "Yeni şifre zorunludur")
        @Size(min = 6, message = "Yeni şifre en az 6 karakter olmalıdır")
        String newPassword,
        
        @NotBlank(message = "Şifre tekrarı zorunludur")
        String confirmPassword
) {
} 