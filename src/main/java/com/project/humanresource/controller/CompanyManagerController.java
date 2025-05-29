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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.project.humanresource.config.RestApis.*;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class CompanyManagerController {

    private final CompanyManagerService companyManagerService;
    private final EmailVerificationService emailVerificationService;
    private final EmployeeService employeeService;

    // başvuruyu employee tablosuna manager olarak kaydeder, isActivated = false, isApproved = false
    @PostMapping(REGISTER)
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
                ResponseEntity.ok("✅ Email activated.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid or expired token.");
    }



    @PutMapping("/dev/v1/updateapplicationstatus/{id}")
    @PreAuthorize("hasAuthority('Admin')")// 26/05 pazartesi 08:19 eklendi  SERKAN
    public ResponseEntity<String> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<Employee> optEmployee = employeeService.findById(id);
        if (optEmployee.isEmpty()) return ResponseEntity.notFound().build();

        Employee employee = optEmployee.get();

        switch (status.toLowerCase()) {
            case "accept":
                employee.setApproved(true);
                emailVerificationService.sendVerificationEmail(employee.getEmail());
                break;
            case "rejected":
                employee.setApproved(false); // gerekirse ayrı rejected alanı ekleyebilirsin
                break;
            case "pending":
                employee.setApproved(false);
                break;
            default:
                return ResponseEntity.badRequest().body("Invalid status");
        }

        employeeService.save(employee);
        return ResponseEntity.ok("Status updated.");
    }




}




