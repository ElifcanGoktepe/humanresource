package com.project.humanresource.dto.response;

import java.time.LocalDateTime;

public record UserProfileResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String profileImageUrl,
        LocalDateTime lastUpdated,
        boolean isActive
) {
} 