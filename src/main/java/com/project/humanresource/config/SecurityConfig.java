package com.project.humanresource.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtUserDetails jwtUserDetails;





    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/approve/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/verify").permitAll()
                        .requestMatchers("/api/set-password").permitAll()
                        .requestMatchers("/assign-manager").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/add-shift").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/list-shift").permitAll()
                        .requestMatchers("/dev/v1/shift","/dev/v1/employee","/dev/v1/user",
                                "/api/users/create_user", "/api/users/login", "/api/users/by-email", "/api/user-roles", "/api/user-roles/by-email",
                                "/api/assignments", "/api/assignments/**",
                                "/swagger-ui/**","/v3/api-docs/**",
                                "/api/auth/**","/api/public/**",
                                "/register"
                        ).permitAll()

                        .requestMatchers("/dev/v1/company/**").hasAuthority("Manager")
                        .requestMatchers("/{id}/upload-profile").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.POST, "/dev/v1/company/add").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.GET,"/dev/v1/company/myCompany").hasAuthority("Manager")
                        .requestMatchers("/dev/v1/companybranch/listAll/{id}").hasAuthority("Manager")
                        .requestMatchers("/dev/v1/department/listAll/{id}").hasAuthority("Manager")
                        .requestMatchers("dev/v1/companybranch/**").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.DELETE, "/dev/v1/companybranch/delete/**").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.DELETE, "/dev/v1/department/delete/{id}").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.PUT,  "/comment/{id}").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.POST,  "/dev/v1/comments/with-photo").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.GET,  "/comments").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.GET,  "/{id}").hasAuthority("Manager")

                        .requestMatchers(HttpMethod.POST, "/dev/v1/department/add").hasAuthority("Manager")
                        .requestMatchers(HttpMethod.GET,"/dev/v1/department/listAllByBranchId/{id}").hasAuthority("Manager")
                        .requestMatchers("/dev/v1/admin/company/pending-applications").hasAuthority("Admin")
                        .requestMatchers("/dev/v1/admin/company/update/{id}").hasAuthority("Admin")
                        .requestMatchers("/dev/v1/admin/**").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.GET," /dev/v1/admin/company/get_all_company_branches_of_selected_company/{id}").hasAuthority("Admin")
                        .requestMatchers(HttpMethod.GET," /dev/v1/admin/company/get_all_company_branches").hasAuthority("Admin")

                        .requestMatchers("/add-employee").hasAuthority("Manager")
                        .requestMatchers("/company-manager/approve/{employeeId}").hasAuthority("Manager")
                        .requestMatchers("/employee/**").hasAuthority("Employee")
                        .requestMatchers("/actives-employees").hasAuthority("Manager")
                        .requestMatchers("/api/users/**").permitAll()
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(jwtUserDetails)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class).build();

    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // frontend portu
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true); // Token vb. için

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}