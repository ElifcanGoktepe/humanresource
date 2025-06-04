package com.project.humanresource.dto.request;
import lombok.Builder;

@Builder
public record EmployeeWithShiftDto(
        String firstName,
        String lastName,
        String shiftStart,
        String shiftEnd


) {
}
