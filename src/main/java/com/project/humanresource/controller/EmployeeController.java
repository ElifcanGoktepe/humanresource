package com.project.humanresource.controller;

import com.project.humanresource.config.JwtTokenFilter;
import com.project.humanresource.dto.request.AddEmployeeRequestDto;

import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.EmployeeResponseDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.User;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.UserRepository;
import com.project.humanresource.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping()
@CrossOrigin("*")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final JwtTokenFilter jwtTokenFilter;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;



   @PutMapping("/employee/activate/"+"{employeeId}")
    public ResponseEntity<BaseResponseShort<Boolean>> activateEmployee(@PathVariable Long employeeId){
        employeeService.setEmployeeActiveStatus(employeeId,true);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee is activated.")
                        .data(true)
                .build());
    }
    @PutMapping("/employee/deactivate/{employeeId}")
    public ResponseEntity<BaseResponseShort<Boolean>> deactivaEmployee(@PathVariable Long employeeId){
        employeeService.setEmployeeActiveStatus(employeeId,false);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee is deactivated.")
                        .data(true)
                .build());
    }

   /*@DeleteMapping("/{employeeId}/delete")
    public ResponseEntity<BaseResponseShort<Boolean>> deleteEmployee(@PathVariable Long employeeId){
        employeeService.deleteEmployeeCompletely(employeeId);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee deleted successfully.")
                        .data(true)
                .build());
    }*/

    @GetMapping("/active-employees")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseShort<List<EmployeeResponseDto>>> getAllEmployeesForManager(
            @RequestHeader("Authorization") String authHeader
    ) {
        // authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9…"
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        // Service katmanını token parametreli olarak çağır:
        List<EmployeeResponseDto> employees = employeeService.getAllEmployeesByToken(token);



        BaseResponseShort<List<EmployeeResponseDto>> response = BaseResponseShort.<List<EmployeeResponseDto>>builder()
                .data(employees)
                .code(200)
                .message("Employees listed")
                .build();

        return ResponseEntity.ok(response);
    }
}






