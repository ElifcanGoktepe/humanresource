package com.project.humanresource.service;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.AddEmployeeForRoleRequirementDto;
import com.project.humanresource.dto.response.EmployeeResponseDto;
import com.project.humanresource.entity.*;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.mapper.EmployeeMapper;
import com.project.humanresource.repository.*;
import com.project.humanresource.utility.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;
    private final JwtManager jwtManager;

    public Optional<Employee> findById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public void addEmployeeForManager(AddEmployeeForRoleRequirementDto dto, HttpServletRequest request) {
        Long managerId = jwtManager.extractUserIdFromToken(request);

        if (managerId == null) {
            throw new HumanResourceException(ErrorType.INVALID_TOKEN, "Manager ID not found in token.");
        }

        if (employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
        }

        Employee employee = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .companyName(dto.companyName())
                .titleName(dto.titleName())
                .managerId(managerId)
                .userRole(UserStatus.Employee)
                .isActive(true)
                .isActivated(false)
                .isApproved(true)
                .build();

        employeeRepository.save(employee);

        emailVerificationService.sendVerificationEmail(employee.getEmail());
    }


    public Employee findEmployeeByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND, "User not found with id: " + userId));

        return employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND, "Employee not found with email: " + user.getEmail()));
    }


    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public Employee getCurrentEmployee(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof JwtUser jwtUser)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "User not authenticated or not a JwtUser instance.");
        }
        String email = jwtUser.getUsername();

        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + email));
    }

    public void deleteById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND);
        }
        employeeRepository.deleteById(id);
    }


    public List<EmployeeResponseDto> getAllEmployeesByToken(String token) {
        Long managerId = jwtManager.extractUserId(token);

        List<Employee> employees = employeeRepository.findAllByIsActivatedTrueAndManagerId(managerId);

        return employees.stream()
                .map(employeeMapper::toEmployeeResponseDto)
                .collect(Collectors.toList());
    }

    public void setEmployeeActiveStatus(Long employeeId, boolean isActive) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        employee.setActive(isActive);
        employeeRepository.save(employee);
    }
}

