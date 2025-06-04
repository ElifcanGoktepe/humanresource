package com.project.humanresource.dto.request;

public record AddCommentDto(
        Long id,
        Long managerId,
        String commentText,
        String photoUrl // yorumla birlikte opsiyonel fotoğraf URL'si (manager photo veya company logo)
) {}
