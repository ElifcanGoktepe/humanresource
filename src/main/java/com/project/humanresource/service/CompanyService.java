package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

        // 2. JWT üzerinden giriş yapan kullanıcıyı JwtUser olarak al
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        // 3. Employee (manager) bulunup companyId güncellensin
        Optional<Employee> employeeOpt = employeeRepository.findById(userId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
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
                company.getBranches()
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

    /**
     * JWT üzerinden o anda giriş yapan kullanıcının (manager) companyId'sini bulup
     * ilgili Company nesnesini DTO'ya çevirerek döner. Eğer user veya company yoksa null döner.
     */
    public AddCompanyRequestDto getMyCompany() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            return null;
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        Optional<Employee> employeeOpt = employeeRepository.findById(userId);
        if (employeeOpt.isEmpty() || employeeOpt.get().getCompanyId() == null) {
            return null;
        }

        Long companyId = employeeOpt.get().getCompanyId();
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        return companyOpt.map(this::toDto).orElse(null);
    }
}
