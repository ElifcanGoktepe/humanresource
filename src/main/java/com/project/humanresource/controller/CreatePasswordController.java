package com.project.humanresource.controller;

import com.project.humanresource.entity.EmailVerification;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.service.EmailVerificationService;
import com.project.humanresource.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class CreatePasswordController {

    private final EmailVerificationService emailVerificationService;
    private final EmployeeService employeeService;
    // şifre oluşturmak için kullanılan metot
    @PostMapping("/api/set-password")
    public ResponseEntity<String> setPassword(@RequestParam String token, @RequestBody String newPassword) {
        Optional<EmailVerification> optional = emailVerificationService.findByToken(token);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid token.");
        }

        EmailVerification verification = optional.get();

        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Expired token.");
        }

        Optional<Employee> optionalEmployee = employeeService.findById(verification.getEmployeeId());
        if (optionalEmployee.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ User not found.");
        }

        Employee employee = optionalEmployee.get();

        // Encoder YOK — şifre direkt yazılıyor (dikkat!)
        employee.setPassword(newPassword);
        employeeService.save(employee);

        return ResponseEntity.ok("✅ Password is created successfully.");
    }
}
