package com.project.humanresource.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {


    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private JwtUserDetails jwtUserDetails;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String requestHeaderAuthorization = request.getHeader("Authorization");

        if (requestHeaderAuthorization != null && requestHeaderAuthorization.startsWith("Bearer ")) {
            String token = requestHeaderAuthorization.substring(7);

            Optional<DecodedJWT> decoded = jwtManager.decodeToken(token);

            if (decoded.isPresent()) {
                DecodedJWT jwt = decoded.get();
                String email = jwt.getSubject(); // ✅ email burada
                Long userId = jwt.getClaim("userId").asLong();
                List<String> roles = jwt.getClaim("roles").asList(String.class);

                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                System.out.println("✅ ROLES: " + roles);
                System.out.println("✅ AUTHORITIES: " + authorities);

                // ✅ Artık principal olarak email atanıyor
                JwtUser jwtUser = (JwtUser) jwtUserDetails.loadUserById(userId);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }

        filterChain.doFilter(request, response);
    }
}
