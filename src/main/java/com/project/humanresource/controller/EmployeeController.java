package com.project.humanresource.controller;

// import com.project.humanresource.config.JwtTokenFilter; // EmployeeService içinde token işlemleri yapılıyor, burada direkt kullanılmıyor gibi.
// import com.project.humanresource.dto.request.AddEmployeeRequestDto; // Kullanılmıyor
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.EmployeeResponseDto;
// import com.project.humanresource.entity.Company; // Kullanılmıyor
// import com.project.humanresource.entity.User; // Kullanılmıyor
// import com.project.humanresource.repository.CompanyRepository; // Controller'da repository direkt kullanılmamalı.
// import com.project.humanresource.repository.UserRepository; // Controller'da repository direkt kullanılmamalı.
import com.project.humanresource.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import jakarta.servlet.http.HttpServletRequest; // Kullanılmıyor
// import jakarta.validation.Valid; // Kullanılmıyor
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder; // Kullanılmıyor
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees") // Controller için ana bir path belirlemek daha iyi olur.
@CrossOrigin("*")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;
    // private final JwtTokenFilter jwtTokenFilter; // Kaldırıldı
    // private final UserRepository userRepository; // Kaldırıldı
    // private final CompanyRepository companyRepository; // Kaldırıldı

   @PutMapping("/activate/{employeeId}") // Path güncellendi: /api/employees/activate/{employeeId}
    public ResponseEntity<BaseResponseShort<Boolean>> activateEmployee(@PathVariable Long employeeId){
        employeeService.setEmployeeActiveStatus(employeeId,true);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee is activated.")
                        .data(true)
                .build());
    }

    @PutMapping("/deactivate/{employeeId}") // Path güncellendi: /api/employees/deactivate/{employeeId}
    public ResponseEntity<BaseResponseShort<Boolean>> deactivateEmployee(@PathVariable Long employeeId){ // Metod adı düzeltildi
        employeeService.setEmployeeActiveStatus(employeeId,false);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Employee is deactivated.")
                        .data(true)
                .build());
    }

    @DeleteMapping("/{employeeId}") // Path güncellendi: /api/employees/{employeeId}
    public ResponseEntity<BaseResponseShort<Void>> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteById(employeeId);
        return ResponseEntity.ok(BaseResponseShort.<Void>builder()
                                .code(200) 
                                .message("Employee deleted successfully.")
                                .build());
    }

    // Bu endpoint zaten /api/employees altında olduğu için path sadece "/active" olabilir veya direkt "/" ile tüm aktifler.
    // Ancak metod adı getAllEmployeesForManager, yani manager altındaki aktif çalışanları getiriyor.
    // Bu durumda path "/manager/active" veya "/managed/active" gibi bir şey daha anlamlı olabilir.
    // Ya da mevcut EmployeeService.getAllEmployeesByToken metodu zaten token'dan managerId alıp ona göre filtreliyor.
    // Şimdilik mevcut path'i koruyup /api/employees/active-employees olarak bırakıyorum.
    @GetMapping("/active-employees") 
    public ResponseEntity<BaseResponseShort<List<EmployeeResponseDto>>> getAllEmployeesForManager(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;

        List<EmployeeResponseDto> employees = employeeService.getAllEmployeesByToken(token);

        BaseResponseShort<List<EmployeeResponseDto>> response = BaseResponseShort.<List<EmployeeResponseDto>>builder()
                .data(employees)
                .code(200)
                .message("Active employees for the manager listed successfully.") // Mesaj güncellendi
                .build();

        return ResponseEntity.ok(response);
    }
}






