package com.bookItNow.booking.security;

import com.bookItNow.booking.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor // Automatically injects JwtService
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final int userId;

        // 1. Check if the header is missing or incorrect
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // 2. Validate token and ensure SecurityContext isn't already set
            if (jwtService.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {

                userId = jwtService.extractUserId(jwt);
                String role = jwtService.extractRole(jwt); // e.g., "USER" or "ADMIN"

                // 3. Create the Authority for Spring Security
                // Spring expects "ROLE_" prefix for hasRole() checks
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                // 4. Create the Authentication Token
                // We use userId as the "Principal" (the name/ID of the user)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        String.valueOf(userId),
                        null,
                        Collections.singletonList(authority)
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Finalize - This "logs in" the user for the duration of this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // If token is tampered with or malformed, we just let it fail at the Auth level
            logger.error("Could not set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}