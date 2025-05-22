package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddCompanyManagerDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.EmailVerification;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.service.CompanyManagerService;
import com.project.humanresource.service.EmailVerificationService;
import com.project.humanresource.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.project.humanresource.config.RestApis.ASSIGN_MANAGER;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class CompanyManagerController {

    private final CompanyManagerService companyManagerService;
    private final EmailVerificationService emailVerificationService;
    private final EmployeeService employeeService;

    // başvuruyu employee tablosuna manager olarak kaydeder, isActivated = false, isApproved = false
    @PostMapping(ASSIGN_MANAGER)
    public ResponseEntity<BaseResponseShort<Boolean>> appliedCompanyManager(@RequestBody AddCompanyManagerDto dto) {
        companyManagerService.appliedCompanyManager(dto);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Company manager applied")
                        .data(true)
                .build());
    }
    // isApproved = true
    @GetMapping("/approve/{employeeId}")
    public ResponseEntity<String> approveManager(@PathVariable Long employeeId) {
        boolean activated = emailVerificationService.approveCompanyManager(employeeId);
        if (activated) {
            return ResponseEntity.ok("✅ Kullanıcı onaylandı ve tamamen aktif hale geldi.");
        } else {
            return ResponseEntity.ok("🟡 Kullanıcı onaylandı ama mail doğrulaması bekleniyor.");
        }
    }
    //isActivated = true
    @GetMapping("/api/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean result = emailVerificationService.verifyToken(token);
        return result ?
                ResponseEntity.ok("✅ Email doğrulandı.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Geçersiz veya süresi dolmuş token.");
    }

    // şifre oluşturmak için kullanılan metot
    @PostMapping("/api/set-password")
    public ResponseEntity<String> setPassword(@RequestParam String token, @RequestBody String newPassword) {
        Optional<EmailVerification> optional = emailVerificationService.findByToken(token);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Token geçersiz.");
        }

        EmailVerification verification = optional.get();

        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Token süresi dolmuş.");
        }

        Optional<Employee> optionalEmployee = employeeService.findById(verification.getEmployeeId());
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Kullanıcı bulunamadı.");
        }

        Employee employee = optionalEmployee.get();

        // Encoder YOK — şifre direkt yazılıyor (dikkat!)
        employee.setPassword(newPassword);
        employeeService.save(employee);

        return ResponseEntity.ok("✅ Şifre başarıyla oluşturuldu (encoder kullanılmadan).");
    }

}
