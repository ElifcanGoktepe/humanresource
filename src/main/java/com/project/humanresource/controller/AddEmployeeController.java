package com.project.humanresource.controller;


import com.project.humanresource.dto.request.AddEmployeeForRoleRequirementDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.humanresource.config.RestApis.ADD_EMPLOYEE;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class AddEmployeeController {
    // Bu sınıf deneme amaçlıdır, managerın employee eklemesi ve izin kısımlarının denenmesi açılmıştır.
    private final EmployeeService employeeService;

    @PostMapping(ADD_EMPLOYEE)
    public ResponseEntity<BaseResponseShort<Boolean>> addEmployee(@RequestBody AddEmployeeForRoleRequirementDto dto, HttpServletRequest request) {
        employeeService.addEmployeeForManager(dto, request);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .data(true)
                        .code(200)
                        .message("Employee is added successfully.")
                .build());



    }

}
