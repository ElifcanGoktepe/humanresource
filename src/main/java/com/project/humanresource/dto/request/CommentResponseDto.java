
package com.project.humanresource.dto.request;


import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,              // BURASI ZORUNLU
        Long managerId,
        String managerName,
        String commentText,
        String photoUrl,
        LocalDateTime createdAt
) {}

