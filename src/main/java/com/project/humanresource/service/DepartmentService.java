
package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.AddDepartmentRequestDto;
import com.project.humanresource.entity.Company;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.entity.Department;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.CompanyBranchRepository;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.DepartmentRepository;
import com.project.humanresource.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Yeni department eklerken:
     * - JWT üzerinden giriş yapan kullanıcının (manager) userId'si alınır.
     * - Employee kaydı bulunur ve ondan manager.getCompanyId() elde edilir.
     * - Eğer DTO içinde companyId varsa, o değer manager'ın companyId'siyle karşılaştırılır; eşleşmiyorsa hata fırlatılır.
     *   DTO.companyId null ise, manager'ın companyId'si kullanılarak Company entity'si atanır.
     * - Benzer şekilde, DTO içinde companyBranchId varsa, o branch'in ait olduğu şirket ID'si manager'ın companyId'siyle karşılaştırılır.
     *   Uyumlu ise branch atanır, değilse hata fırlatılır. DTO.companyBranchId null ise şube ataması yapılmaz.
     */
    public Department addDepartment(AddDepartmentRequestDto dto) {
        // 1. JWT üzerinden giriş yapan kullanıcıyı JwtUser olarak al
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        // 2. Employee (manager) kaydını bul ve manager'ın companyId'sini al
        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userId));
        Long managerCompanyId = manager.getCompanyId();
        if (managerCompanyId == null) {
            throw new RuntimeException("Manager does not have a company assigned yet.");
        }

        // 3. Yeni department nesnesini oluştur
        Department department = Department.builder()
                .departmentName(dto.departmentName())
                .departmentCode(dto.departmentCode())
                .build();

        // 4. DTO içinde companyId var mı?
        if (dto.companyId() != null) {
            // a) Verilen companyId'nin manager'ın companyId'siyle aynı olup olmadığını kontrol et
            if (!dto.companyId().equals(managerCompanyId)) {
                throw new RuntimeException("Unauthorized to add department for company id: " + dto.companyId());
            }
            // b) Eşleşiyorsa Company entity'sini ata
            Company company = companyRepository.findById(managerCompanyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + managerCompanyId));
            department.setCompany(company);
        } else {
            // DTO içinde companyId yoksa, manager'ın companyId'sini kullan
            Company company = companyRepository.findById(managerCompanyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with id: " + managerCompanyId));
            department.setCompany(company);
        }

        // 5. DTO içinde companyBranchId var mı?
        if (dto.companyBranchId() != null) {
            // a) Verilen branchId'yi bul, yoksa hata
            CompanyBranch branch = companyBranchRepository.findById(dto.companyBranchId())
                    .orElseThrow(() -> new RuntimeException("CompanyBranch not found with id: " + dto.companyBranchId()));
            // b) Branch'in ait olduğu companyId ile manager'ın companyId'sini karşılaştır
            Long branchCompanyId = branch.getCompany().getId();
            if (!branchCompanyId.equals(managerCompanyId)) {
                throw new RuntimeException("Unauthorized to add department under branch id: " + dto.companyBranchId());
            }
            department.setCompanyBranch(branch);
        } // Eğer DTO.companyBranchId null ise, şube ataması yapılmaz

        // 6. Kaydet ve döndür

        Department savedDepartment = departmentRepository.save(department);

        return savedDepartment;
    }

    /**
     * ID'ye göre department bul. Bulamazsa EntityNotFoundException fırlatır.
     */
    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
    }

    /**
     * Tüm department'ları listeler.
     */
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    /**
     * ID'ye göre silme işlemi yapar.
     */
    public Department deleteDepartment(Long id) {
        // 1. Department var mı kontrol et
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found with id: " + id));

        // 2. JWT üzerinden giriş yapan kullanıcının (manager) bilgilerini al
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (!(principal instanceof JwtUser)) {
            throw new RuntimeException("Authentication principal is not of type JwtUser");
        }
        JwtUser jwtUser = (JwtUser) principal;
        Long userId = jwtUser.getUserId();

        // 3. Manager’ın şirket adı (ya da id’si)
        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + userId));
        String managerCompanyName = manager.getCompanyName();

        // 4. Department’ın ait olduğu şirket adı (burada Department entity içinde company ilişkisi varsa onu kullan)
        String departmentCompanyName = department.getCompany().getCompanyName();

        // 5. Karşılaştır: sadece kendi şirketine ait department silinebilir
        if (!managerCompanyName.equalsIgnoreCase(departmentCompanyName)) {
            throw new RuntimeException("Unauthorized to delete this department");
        }

        // 6. Sil ve geri döndür
        departmentRepository.delete(department);
        return department;
    }

    /**
     * DepartmentCode'a göre department bulur. Bulamazsa EntityNotFoundException fırlatır.
     */
    public Department findByDepartmentCode(String code) {
        Department department = departmentRepository.findByDepartmentCode(code);
        if (department == null) {
            throw new EntityNotFoundException("Department not found with code: " + code);
        }
        return department;
    }

    /**
     * Branch ID'ye göre department listesi döner.
     * Her department, kendi DTO yapısına çevrilir.
     */
    public List<AddDepartmentRequestDto> getDepartmentsByBranchId(Long branchId) {
        return departmentRepository.findByCompanyBranchId(branchId)
                .stream()
                .map(d -> new AddDepartmentRequestDto(
                        d.getId(),   // burada id'yi de ekliyoruz
                        d.getDepartmentName(),
                        d.getDepartmentCode(),
                        d.getCompany() != null ? d.getCompany().getId() : null,
                        d.getCompanyBranch() != null ? d.getCompanyBranch().getId() : null
                ))
                .collect(Collectors.toList());
    }
}
