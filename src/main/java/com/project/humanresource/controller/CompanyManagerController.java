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
@CrossOrigin("/company-manager/")
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
            return ResponseEntity.ok("✅ The user has been approved and is now fully active.");
        } else {
            return ResponseEntity.ok("🟡 The user has been approved, but email verification is pending.");
        }
    }
    //isActivated = true
    @GetMapping("/api/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean result = emailVerificationService.verifyToken(token);
        return result ?
                ResponseEntity.ok("✅ Email activated. Please check your mailbox to create password.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid or expired token.");
    }
}
