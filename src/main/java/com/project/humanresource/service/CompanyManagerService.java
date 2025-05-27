package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCompanyManagerDto;
import com.project.humanresource.dto.request.AddRoleRequestDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.UserRoleRepository;
import com.project.humanresource.utility.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyManagerService {

    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationService emailVerificationService;

    public void appliedCompanyManager(AddCompanyManagerDto dto) {

        // ✅ 1. Aynı email ile daha önce başvuru yapılmış mı kontrol et  26/05 12:00 serkan kılıçdere
        Optional<Employee> existing = employeeRepository.findByEmail(dto.email());
        if (existing.isPresent()) {
            throw new IllegalStateException("Bu e-posta ile zaten bir başvuru yapılmış.");
        }

        Employee manager = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .companyName(dto.companyName())
                .titleName(dto.titleName())
                .isActivated(false)
                .isApproved(false)
                .isActive(false)
                .build();
        manager = employeeRepository.save(manager);  // 26/ 05 güncellendi serkan

        UserRole managerRole = UserRole.builder()
                .userStatus(UserStatus.Manager)
                .userId(manager.getId())  // artık kesinlikle manager.getId() dolu
                .build();

        userRoleRepository.save(managerRole);

        emailVerificationService.sendApprovalRequestToAdmin(manager);
    }

    public List<Employee> getAllPendingManagers() {                                // 26/05 pazartesi 08:19 eklendi serkan
        return employeeRepository.findByIsApprovedFalse();

    }




}

