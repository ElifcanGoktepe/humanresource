package com.project.humanresource.dto.response;

public record EmployeeBasicInfoDto(
    Long id,
    String firstName,
    String lastName,
    String email
) {} 