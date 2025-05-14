package com.project.humanresource.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtUserDetails jwtUserDetails;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/dev/v1/shift","/dev/v1/employee","/dev/v1/user",
                                "/api/users/create_user", "/api/user-roles",
                                "/swagger-ui/**","/v3/api-docs/**",
                                "/api/auth/**","/api/public/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("SITE_ADMIN")
                        .requestMatchers("/company-manager/**").hasAuthority("COMPANY_ADMIN")
                        .requestMatchers("/employee/**").hasAuthority("EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(jwtUserDetails)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class).build();

    }
}