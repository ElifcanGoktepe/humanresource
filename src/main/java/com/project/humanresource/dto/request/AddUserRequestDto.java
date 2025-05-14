package com.project.humanresource.dto.request;

public record AddUserRequestDto(
        String email,
        String password,
        String rePassword
) {}