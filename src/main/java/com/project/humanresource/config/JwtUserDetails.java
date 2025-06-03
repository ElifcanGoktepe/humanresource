package com.project.humanresource.config;

import com.project.humanresource.entity.Employee;
// import com.project.humanresource.entity.UserRole; // Kaldırıldı
import com.project.humanresource.service.EmployeeService;
// import com.project.humanresource.service.UserRoleService; // Kaldırıldı
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Tek rol için
import java.util.List;
import java.util.Optional;
// import java.util.stream.Collectors; // Artık stream'e gerek yok

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {

    private final EmployeeService employeeService;
    // private final UserRoleService userRoleService; // Kaldırıldı

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Projenizde email ile kullanıcı bulma varsa, burası implement edilebilir.
        // Örn: Optional<Employee> employeeOpt = employeeService.findByEmail(username);
        // Şimdilik ID ile yükleme destekleniyor.
        throw new UnsupportedOperationException("Username login is not supported. Use loadUserById or loadUserByEmail instead.");
    }

    public UserDetails loadUserById(Long userId) {
        Optional<Employee> employeeOpt = employeeService.findById(userId);

        if (employeeOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        Employee employee = employeeOpt.get();

        // Roller doğrudan Employee entity'sinden alınıyor
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(employee.getUserRole().name())
        );

        return new JwtUser(
                employee.getId(),
                employee.getEmail(),
                employee.getPassword(), // Şifre burada UserDetails için gerekli, DB'deki hashlenmiş hali olmalı
                employee.getFirstName(),
                employee.getLastName(),
                employee.getTitleName(),
                employee.getCompanyName(),
                authorities,
                true,  // isAccountNonExpired
                true,  // isAccountNonLocked
                true,  // isCredentialsNonExpired
                employee.isActivated() && employee.isActive() && employee.isApproved() // isEnabled durumu
        );
    }
}
