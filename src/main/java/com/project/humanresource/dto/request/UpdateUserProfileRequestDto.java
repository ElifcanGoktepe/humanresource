package com.project.humanresource.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequestDto(
        @NotBlank(message = "Ad alanı zorunludur")
        @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır")
        String firstName,
        
        @NotBlank(message = "Soyad alanı zorunludur")
        @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır")
        String lastName,
        
        @NotBlank(message = "E-posta alanı zorunludur")
        @Email(message = "Geçerli bir e-posta adresi giriniz")
        String email,
        
        @Size(min = 10, max = 15, message = "Telefon numarası 10-15 haneli olmalıdır")
        String phone
) {
} 