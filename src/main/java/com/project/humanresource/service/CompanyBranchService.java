package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyBranchRepository;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyBranchService {

    private final CompanyBranchRepository companyBranchRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Yeni şube ekleme: JWT üzerinden giriş yapan kullanıcı (manager) elde edilir,
     * şirket adı alınır, o şirkete bağlı branch oluşturulur.
     */
    public CompanyBranch addCompanyBranch(AddCompanyBranchRequestDto dto) {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userId));

        Company company = companyRepository.findByCompanyNameIgnoreCase(manager.getCompanyName())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Company not found with name: " + manager.getCompanyName()));

        CompanyBranch companyBranch = CompanyBranch.builder()
                .companyBranchCode(dto.companyBranchCode())
                .companyBranchAddress(dto.companyBranchAddress())
                .companyBranchPhoneNumber(dto.companyBranchPhoneNumber())
                .companyBranchEmailAddress(dto.companyBranchEmailAddress())
                .company(company)
                .build();

        return companyBranchRepository.save(companyBranch);
    }

    /**
     * Şirket şubesini silme: JWT üzerinden giriş yapan kullanıcı (manager) elde edilir,
     * silinmek istenen branch’ın bağlı olduğu şirket ile manager’ın şirket adı karşılaştırılır.
     * Aynı şirketten değilse yetkisiz hata fırlatılır.
     */
    public CompanyBranch deleteCompanyBranch(Long id) {
        // 1. Branch var mı kontrol et
        CompanyBranch companyBranch = companyBranchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company branch not found with id: " + id));

        // 2. JWT üzerinden giriş yapan kullanıcının (manager) bilgilerini al
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        // 3. Manager’ın şirket adı
        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userId));
        String managerCompanyName = manager.getCompanyName();

        // 4. Branch’ın ait olduğu şirket adı
        String branchCompanyName = companyBranch.getCompany().getCompanyName();

        // 5. Karşılaştır: sadece kendi şirketine ait branch silinebilir
        if (!managerCompanyName.equalsIgnoreCase(branchCompanyName)) {
            throw new RuntimeException("Unauthorized to delete this branch");
        }

        // 6. Sil ve geri döndür
        companyBranchRepository.delete(companyBranch);
        return companyBranch;
    }

    /**
     * Tüm şubenin listesini döner.
     */
    public List<CompanyBranch> findAll() {
        return companyBranchRepository.findAll();
    }

    /**
     * Adrese göre şube arar, yoksa hata fırlatır.
     */
    public CompanyBranch findByCompanyBranchAddress(String address) {
        if (!companyBranchRepository.existsByCompanyBranchAddress(address)) {
            throw new EntityNotFoundException("Company branch address not found: " + address);
        }
        return companyBranchRepository.findByCompanyBranchAddress(address);
    }

    /**
     * Email adresine göre şube arar, bulamazsa null döner.
     */
    public CompanyBranch findByCompanyBranchEmailAddress(String email) {
        return companyBranchRepository.findByCompanyBranchEmailAddress(email)
                .orElse(null);
    }

    /**
     * Telefon numarasına göre şube arar, yoksa hata fırlatır.
     */
    public CompanyBranch findByCompanyBranchPhoneNumber(String phoneNumber) {
        if (!companyBranchRepository.existsByCompanyBranchPhoneNumber(phoneNumber)) {
            throw new EntityNotFoundException(
                    "Company branch phone number not found: " + phoneNumber);
        }
        return companyBranchRepository.findByCompanyBranchPhoneNumber(phoneNumber);
    }
}
