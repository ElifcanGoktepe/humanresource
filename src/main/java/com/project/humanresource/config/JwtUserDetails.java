
package com.project.humanresource.config;

import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.service.EmployeeService;
import com.project.humanresource.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {

    private final EmployeeService employeeService;
    private final UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Username login is not supported. Use loadUserById instead.");

    }

    public UserDetails loadUserById(Long userId) {
        Optional<Employee> employeeOpt = employeeService.findById(userId);

        if (employeeOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        Employee employee = employeeOpt.get();

        List<UserRole> userRoles = userRoleService.findAllRole(userId);

        List<SimpleGrantedAuthority> authorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getUserStatus().name()))
                .collect(Collectors.toList());

        return new JwtUser(
                employee.getId(),
                employee.getEmail(),
                employee.getPassword(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getTitleName(),
                employee.getCompanyName(),
                authorities,
                true,  // isAccountNonExpired
                true,  // isAccountNonLocked
                true,  // isCredentialsNonExpired
                true   // isEnabled (Aktiflik durumu için isActivated kullanabilirsiniz)
        );
    }
}
