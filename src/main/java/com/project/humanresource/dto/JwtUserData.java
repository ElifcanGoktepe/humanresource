
package com.project.humanresource.dto;

import com.project.humanresource.utility.UserStatus;

public record JwtUserData(
        Long userId,
        String email,
        UserStatus role
) {
}
