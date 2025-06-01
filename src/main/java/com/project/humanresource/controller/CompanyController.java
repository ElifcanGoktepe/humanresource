package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;
import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.service.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.humanresource.config.RestApis.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev/v1/company")
@CrossOrigin("*")
public class CompanyController {

    private final CompanyService companyService;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    // Yeni şirket oluştur
    @PostMapping("/add")
    public ResponseEntity<AddCompanyRequestDto> createCompany(@RequestBody AddCompanyRequestDto dto) {
        return ResponseEntity.ok(companyService.addCompany(dto));
    }


    // Şirket güncelle
    @PutMapping("/update/{id}")
    public ResponseEntity<AddCompanyRequestDto> updateCompany(@PathVariable Long id, @RequestBody AddCompanyRequestDto dto) {
        AddCompanyRequestDto updatedCompany = companyService.updateCompany(id, dto);
        return updatedCompany != null ? ResponseEntity.ok(updatedCompany) : ResponseEntity.notFound().build();
    }



    @GetMapping("/myCompany")
    public ResponseEntity<?> getMyCompany() {
        try {
            AddCompanyRequestDto dto = companyService.getMyCompany();
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found or user has no associated company");
            }
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }
    }







