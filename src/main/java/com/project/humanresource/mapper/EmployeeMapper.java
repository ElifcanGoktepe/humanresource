package com.project.humanresource.mapper;

import com.project.humanresource.dto.response.EmployeeResponseDto;
import com.project.humanresource.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeResponseDto toEmployeeResponseDto(Employee employee) {
        String fullName = employee.getFirstName() + " " + employee.getLastName();
        return new EmployeeResponseDto(
                fullName,
                employee.getEmail(),
                employee.getphoneNumber(),
                employee.getTitleName(),
                employee.isActive()
        );
    }
}
