package com.project.humanresource.mapper;

import com.project.humanresource.dto.response.EmployeeResponseDto;
import com.project.humanresource.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        String fullName = employee.getFirstName() + " " + employee.getLastName();
        return new EmployeeResponseDto(
                employee.getId(),
                fullName,
                employee.getEmail(),
                employee.getPhoneNumber(),
                employee.getTitleName(),
                employee.isActive()
        );
    }
}
