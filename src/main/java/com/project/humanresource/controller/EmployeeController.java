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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping()
@CrossOrigin("*")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final JwtTokenFilter jwtTokenFilter;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;



  /*  @PutMapping("/activate/{employeeId}")
    public ResponseEntity<BaseResponseShort<Boolean>> activateEmployee(@PathVariable Long employeeId){
        employeeService.setEmployeeActiveStatus(employeeId,true);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee is activated.")
                        .data(true)
                .build());
    }
    @PutMapping("/deactive/{employeeId}")
    public ResponseEntity<BaseResponseShort<Boolean>> deactivaEmployee(@PathVariable Long employeeId){
        employeeService.setEmployeeActiveStatus(employeeId,false);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee is deactivated.")
                        .data(true)
                .build());
    }*/

  /*  @DeleteMapping("/{employeeId}/delete")
    public ResponseEntity<BaseResponseShort<Boolean>> deleteEmployee(@PathVariable Long employeeId){
        employeeService.deleteEmployeeCompletely(employeeId);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee deleted successfully.")
                        .data(true)
                .build());
    }*/

   /* @GetMapping("/employee/get-all")
    public ResponseEntity<BaseResponseShort<List<EmployeeResponseDto>>> getAllEmployeesForManager() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println("Emai,l : "+email);

        List<EmployeeResponseDto> employees = employeeService.getAllEmployeesForManager();

        return ResponseEntity.ok(BaseResponseShort.<List<EmployeeResponseDto>>builder()
                .code(200)
                .message("Employees listed")
                .data(employees)
                .build());
    }*/



//
////    //@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
////    @PostMapping("/assign-title")
////    public ResponseEntity<BaseResponseShort<Boolean>> assignTitleToemployee(@RequestBody AssignTitleToEmployeeRequestDto dto){
////        employeeService.assignTitleToEmployee(dto);
////
////        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
////                .code(200)
////                .data(true)
////                .message("Unvan çalışana başarıyla atandı")
////                .build());
////    }

}
