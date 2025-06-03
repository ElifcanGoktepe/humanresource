package com.project.humanresource.dto.response;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long managerId,
        String managerName,
        String commentText,
        String photoUrl,
        LocalDateTime createdAt,
        Long commenterId,
        String commenterName
) {
} 