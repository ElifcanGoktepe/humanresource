package com.project.humanresource.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequestDto(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-ZğüşıöçĞÜŞİÖÇ\\s]+$", message = "First name must contain only letters and spaces")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-ZğüşıöçĞÜŞİÖÇ\\s]+$", message = "Last name must contain only letters and spaces")
        String lastName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @Pattern(regexp = "^[+]?[0-9\\s]+$", message = "Phone number must contain only numbers, plus sign and spaces")
        @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
        String phone
) {
} 