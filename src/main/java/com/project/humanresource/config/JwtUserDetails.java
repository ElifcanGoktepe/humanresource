package com.project.humanresource.config;

import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.service.EmployeeService;
import com.project.humanresource.service.UserRoleService;
import com.project.humanresource.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final EmployeeService employeeService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("Username login is not supported. Use loadUserById instead.");
    }

    public UserDetails loadUserById(Long userId) {
        Optional<Employee> user = employeeService.findById(userId);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with ID: " + userId); // bakılacak
            //return null;
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();  // user role servisinden userId sine ait rollerin listesini çekiyoruz.
        List<UserRole> userRolesList =userRoleService.findAllRole(userId); // bu role listesini grandauthority listesine ekliyoruz.

        userRolesList.forEach(userRole -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRole.getUserStatus().name()));
        });

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.get().getEmail())
                .password(user.get().getPassword())
                .accountLocked(false)
                .accountExpired(false)
                .authorities(grantedAuthorities)
                .build();


    }
}
