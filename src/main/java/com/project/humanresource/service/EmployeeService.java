package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddEmployeeForRoleRequirementDto;
import com.project.humanresource.dto.request.AddEmployeeRequestDto;
import com.project.humanresource.dto.request.SetPersonalFileRequestDto;
import com.project.humanresource.entity.*;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.*;
import com.project.humanresource.utility.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationService emailVerificationService;
    private final PersonelFileRepository personelFileRepository;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final CompanyRepository companyRepository;

    public Optional<Employee> findById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public void addEmployeeForManager(AddEmployeeForRoleRequirementDto dto) { //manager tarafından eklenen employee
        Employee employee = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .emailWork(dto.emailWork())
                .phoneWork(dto.phoneWork())
                .companyName(dto.companyName())
                .titleName(dto.titleName())
                .isApproved(true)
                .build();
        employeeRepository.save(employee);

        UserRole employeeRole = UserRole.builder()
                .userStatus(UserStatus.Employee)
                .userId(employee.getId())
                .build();
        userRoleRepository.save(employeeRole);

        emailVerificationService.sendVerificationEmail(employee.getEmailWork());

    }

    public void  addEmployee(AddEmployeeRequestDto dto,Long companyId) {
        // 1. Login email sistemde var mı?
        if (userRepository.existsByEmail(dto.email())) {
            throw new HumanResourceException(ErrorType.USER_ALREADY_EXISTS);
        }


// 2. Login User oluşturulmaz (User abstract) → doğrudan Employee oluşturulacak
        Employee employee   =Employee.builder()
                .firstName(dto.name())
                .lastName(dto.surname())
                .email(dto.email())
                .phoneWork(dto.phoneNumber())
                .companyId(companyId)
                .isActive(false)
                .password(null)
                .titleId(dto.titleId())
                .build();
        // 3. Kullanıcı rolü ata
        UserRole userRole=UserRole.builder()
                .userId(employee.getId())
                .userStatus(UserStatus.Employee)
                .build();

        employeeRepository.save(employee);
        userRoleRepository.save(userRole);

        // 4. Aktivasyon maili gönder (şifre oluşturma bağlantısı dahil)
        emailVerificationService.sendVerificationEmail(dto.email());
    }

    public void setEmployeeActiveStatus (Long employeeId, boolean isActive) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new HumanResourceException(ErrorType.USER_NOT_FOUND));

        if (!userRoleService.hasRole(user.getId(),UserStatus.Manager)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
        }



        Company company=companyRepository.findByEmployerId(user.getId())
                .orElseThrow(()->new HumanResourceException(ErrorType.COMPANY_NOT_FOUND));

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        if(!employee.getCompanyId().equals(company.getId())){
            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
        }
        employee.setActive(isActive);
        employeeRepository.save(employee);
    }

    public void deleteEmployeeCompletely(Long employeeId) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();

        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new HumanResourceException(ErrorType.USER_NOT_FOUND));
        if (!userRoleService.hasRole(user.getId(),UserStatus.Manager)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
        }
        Company company=companyRepository.findByEmployerId(user.getId())
                .orElseThrow(()->new HumanResourceException(ErrorType.COMPANY_NOT_FOUND));

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        if(!employee.getCompanyId().equals(company.getId())){
            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
        }

        // 1. personelin özlük dosyasını sil
        Optional.ofNullable(employee.getPersonalFiledId())
                .flatMap(personelFileRepository::findById)
                .ifPresent(personelFileRepository::delete);

        userRoleRepository.deleteByUserId(employee.getId());

        userRepository.deleteById(user.getId());

        employeeRepository.deleteById(employeeId);


    }

//    public void deleteEmployee (Long employeeId) {
//        String email=SecurityContextHolder.getContext().getAuthentication().getName();
//        User user=userRepository.findByEmail(email)
//                .orElseThrow(()->new HumanResourceException(ErrorType.USER_NOT_FOUND));
//        UserRole userRole= (UserRole) userRoleService.findAllRole(user.getId());
//
//        if (!userRole.getUserStatus().equals(UserStatus.Manager)){
//            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
//        }
//        Company company=companyRepository.findByEmployerId(user.getId())
//                .orElseThrow(()->new HumanResourceException(ErrorType.COMPANY_NOT_FOUND));
//
//        Employee employee=employeeRepository.findById(employeeId)
//                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
//
//        if(!employee.getCompanyId().equals(company.getId())){
//            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
//        }
//
//        if (employee.getPersonalFiledId()!=null){
//            personelFileRepository.findById(employee.getPersonalFiledId())
//                    .ifPresent(personelFileRepository::delete);
//        }
//
//        if (employee.){
//            employeeRepository.
//        }
//    }



//    private final EmployeeRepository employeeRepository;
//
//    // Geçici: email → employeeId eşlemesi için in-memory map
//    private static final ConcurrentHashMap<String, Long> emailToEmployeeMap = new ConcurrentHashMap<>();
//    private final UserRepository userRepository;
//    private final PersonelFileRepository personelFileRepository;
//    private final CompanyRepository companyRepository;
//    //private final CompanyTitleRepository companyTitleRepository;
//
//
//
//
//    // Şifre oluşturulduktan sonra eşleştirme işlemi yapılır
//    public void assignUserToEmployee(String email,Long userId){
//        Long employeeId=emailToEmployeeMap.get(email);
//        if (employeeId !=null){
//            Employee employee=employeeRepository.findById(employeeId)
//                    .orElseThrow(()->new RuntimeException("Employe bulunamadı"));
//            employee.setUserId(userId);
//            employeeRepository.save(employee);
//            emailToEmployeeMap.remove(email); // eşleştirme tamamlandıysa map’ten sil
//        }
//
//    }
//

//
//
//    public void assignTitleToEmployee(AssignTitleToEmployeeRequestDto dto) {
//        String email= SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User user=userRepository.findByEmail(email)
//                .orElseThrow(()->new HumanResourceException(ErrorType.USER_NOT_FOUND));
//
//        if (!user.getUserRoleId().equals(UserStatus.COMPANY_ADMIN.ordinal())){
//            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
//        }
//
//        Company company=companyRepository.findByUserId(user.getId())
//                .orElseThrow(()->new HumanResourceException(ErrorType.COMPANY_NOT_FOUND));
//
//        Employee employee=employeeRepository.findById(dto.employeeId())
//                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));
//
//        if (!employee.getCompanyId().equals(company.getId())){
//            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
//        }
//
//        if (!companyTitleRepository.existsByCompanyIdAndTitleId(company.getId(),dto.titleId())){
//            throw new HumanResourceException(ErrorType.UNAUTHORIZED);
//        }
//
//        employee.setTitleId(dto.titleId());
//        employeeRepository.save(employee);
//    }



}
