package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddEmployeeForRoleRequirementDto;
import com.project.humanresource.dto.request.AddEmployeeRequestDto;
import com.project.humanresource.dto.response.EmployeeResponseDto;
import com.project.humanresource.entity.*;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.mapper.EmployeeMapper;
import com.project.humanresource.repository.*;
import com.project.humanresource.utility.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.humanresource.config.SecurityUtil;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationService emailVerificationService;
    private final PersonelFileRepository personelFileRepository;
    private final UserRepository userRepository;


    private final EmployeeMapper employeeMapper;


    public Optional<Employee> findById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public void addEmployeeForManager(AddEmployeeForRoleRequirementDto dto,  HttpServletRequest request) { //manager tarafından eklenen employee
        Long managerId = (Long) request.getAttribute("userId");

        if (managerId == null) {
            throw new IllegalStateException("Manager ID not found in request.");
        }
        Employee employee = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
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

        emailVerificationService.sendVerificationEmail(employee.getEmail());

    }

    public void setEmployeeActiveStatus (Long employeeId, boolean isActive) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));


        employee.setActive(isActive);
        employeeRepository.save(employee);
    }
//
    public void deleteEmployeeCompletely(Long employeeId) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();

        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new HumanResourceException(ErrorType.USER_NOT_FOUND));



        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));



        // 1. personelin özlük dosyasını sil
        Optional.ofNullable(employee.getPersonalFiledId())
                .flatMap(personelFileRepository::findById)
                .ifPresent(personelFileRepository::delete);

        userRoleRepository.deleteByUserId(employee.getId());

        userRepository.findById(employee.getId())
                .ifPresent(us -> userRepository.deleteById(user.getId()));

        employeeRepository.deleteById(employeeId);


    }

          public List<EmployeeResponseDto> getAllEmployeesForManager() {
//        System.out.println("Email ile gelen kullanıcı :"+email);
//        User manager = userRepository.findByEmail(email)
//                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));
//
//        System.out.println("Maanger ID : "+manager.getId());
              Long managerId= SecurityUtil.getCurrentUserId();


        return employeeRepository.findAllByManagerId(managerId)
                .stream()
                .map(employeeMapper::toEmployeeResponseDto)
                .toList();
    }





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
