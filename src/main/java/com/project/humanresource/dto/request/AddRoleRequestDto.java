package com.project.humanresource.dto.request;

import com.project.humanresource.utility.UserStatus;

public record AddRoleRequestDto(
        UserStatus userStatus,
        Long userId
) {}