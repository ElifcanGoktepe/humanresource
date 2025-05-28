package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.humanresource.config.RestApis.*;

@RestController
@RequiredArgsConstructor
@RequestMapping // Base URL sabitlerde
@CrossOrigin("*")
public class CompanyController {

    private final CompanyService companyService;

    // Yeni şirket oluştur
    @PostMapping(ADD_COMPANY)
    public ResponseEntity<AddCompanyRequestDto> createCompany(@RequestBody AddCompanyRequestDto dto) {
        return ResponseEntity.ok(companyService.addCompany(dto));
    }

    // Tüm şirketleri getir
    @GetMapping(GET_ALL_COMPANIES)
    public ResponseEntity<List<AddCompanyRequestDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    // ID'ye göre şirket getir
    @GetMapping(GET_COMPANY_BY_ID)
    public ResponseEntity<AddCompanyRequestDto> getCompanyById(@PathVariable Long id) {
        AddCompanyRequestDto company = companyService.getCompanyById(id);
        return company != null ? ResponseEntity.ok(company) : ResponseEntity.notFound().build();
    }

    // Şirket güncelle
    @PutMapping(UPDATE_COMPANY)
    public ResponseEntity<AddCompanyRequestDto> updateCompany(@PathVariable Long id, @RequestBody AddCompanyRequestDto dto) {
        AddCompanyRequestDto updatedCompany = companyService.updateCompany(id, dto);
        return updatedCompany != null ? ResponseEntity.ok(updatedCompany) : ResponseEntity.notFound().build();
    }

    // Şirket sil
    @DeleteMapping(DELETE_COMPANY)
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    // Şirket adıyla arama
    @GetMapping(SEARCH_COMPANY_BY_NAME)
    public ResponseEntity<List<AddCompanyRequestDto>> searchCompaniesByName(@RequestParam String name) {
        return ResponseEntity.ok(companyService.searchCompaniesByName(name));
    }

    // Şirket e-posta güncelle
    @PatchMapping(UPDATE_COMPANY_EMAIL)
    public ResponseEntity<AddCompanyRequestDto> updateCompanyEmail(@PathVariable Long id, @RequestParam String email) {
        AddCompanyRequestDto updated = companyService.updateCompanyEmail(id, email);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping(MY_COMPANY)
    public ResponseEntity<AddCompanyRequestDto> getMyCompany() {
        AddCompanyRequestDto myCompany = companyService.getMyCompany();
        if (myCompany != null) {
            return ResponseEntity.ok(myCompany);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
