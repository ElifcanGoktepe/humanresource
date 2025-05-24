package com.project.humanresource.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtManager jwtManager;
    @Autowired
    private JwtUserDetails jwtUserDetails;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String requestHeaderAuthorization = request.getHeader("Authorization");
        System.out.println("header....: " + requestHeaderAuthorization);

        if (Objects.nonNull(requestHeaderAuthorization) && requestHeaderAuthorization.startsWith("Bearer ")) {
            String token = requestHeaderAuthorization.substring(7);
            Optional<Long> userId = jwtManager.validateToken(token);

            if (userId.isPresent()) {
                request.setAttribute("userId", userId.get());
                UserDetails userDetails = jwtUserDetails.loadUserById(userId.get());

                // 🔍 Buraya log ekledik
                System.out.println("✅ userDetails.getAuthorities(): " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken springToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(springToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
