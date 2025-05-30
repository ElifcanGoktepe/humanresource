package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.entity.Company;

import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyRepository;

import com.project.humanresource.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;


    public List<AddCompanyRequestDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddCompanyRequestDto getCompanyById(Long id) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        return companyOpt.map(this::toDto).orElse(null);
    }

    @Transactional
    public AddCompanyRequestDto addCompany(AddCompanyRequestDto dto) {
        // 1. Şirketi kaydet
        Company company = toEntity(dto);
        Company saved = companyRepository.save(company);

        // 2. Giriş yapan kullanıcıyı bul (id üzerinden)
        String idStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = Long.parseLong(idStr);
        Optional<Employee> employeeOpt = employeeRepository.findById(userId);

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            // 3. employee'nin companyId'sini kaydedilen şirketin id'si ile güncelle
            employee.setCompanyId(saved.getId());
            employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Logged in user not found!");
        }

        // 4. DTO'yu geri döndür
        return toDto(saved);
    }


    public AddCompanyRequestDto updateCompany(Long id, AddCompanyRequestDto dto) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            company.setCompanyName(dto.companyName());
            company.setCompanyAddress(dto.companyAddress());
            company.setCompanyPhoneNumber(dto.companyPhoneNumber());
            company.setCompanyEmail(dto.companyEmail());

            return toDto(companyRepository.save(company));
        }
        return null;
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public List<AddCompanyRequestDto> searchCompaniesByName(String name) {
        return companyRepository.findByCompanyNameContainingIgnoreCase(name).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddCompanyRequestDto updateCompanyEmail(Long id, String email) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            company.setCompanyEmail(email);
            return toDto(companyRepository.save(company));
        }
        return null;
    }

    private AddCompanyRequestDto toDto(Company company) {
        return new AddCompanyRequestDto(
                company.getId(),
                company.getCompanyName(),
                company.getCompanyPhoneNumber(),
                company.getCompanyAddress(),
                company.getCompanyEmail(),
                company.getBranches()  // branches listesi de eklenmeli
        );
    }


    private Company toEntity(AddCompanyRequestDto dto) {
        return Company.builder()
                .id(dto.id())
                .companyName(dto.companyName())
                .companyAddress(dto.companyAddress())
                .companyPhoneNumber(dto.companyPhoneNumber())
                .companyEmail(dto.companyEmail())
                .build();
    }


    public AddCompanyRequestDto getMyCompany() {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<Employee> employeeOpt = employeeRepository.findById(userId);
        if (employeeOpt.isEmpty() || employeeOpt.get().getCompanyId() == null) {
            return null;
        }

        Optional<Company> companyOpt = companyRepository.findById(employeeOpt.get().getCompanyId());
        if (companyOpt.isEmpty()) {
            return null;
        }

        return toDto(companyOpt.get());
    }


    }
