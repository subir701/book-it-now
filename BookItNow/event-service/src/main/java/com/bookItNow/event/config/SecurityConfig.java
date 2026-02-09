package com.bookItNow.event.config;

import com.bookItNow.event.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // CRITICAL: This enables @PreAuthorize("hasRole('...')")
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Disable CSRF as we are using JWT (stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configure endpoint protection
                .authorizeHttpRequests(auth -> auth
                        // 1. Allow everyone to view events
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bookitnow/v1/events/upcoming").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bookitnow/v1/events/available").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bookitnow/v1/events/venue/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/bookitnow/v1/seats/{eventId}/available").permitAll()

                        // 2. Allow Swagger/API Docs to be public (optional but helpful)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 3. Everything else (Add, Update, Delete) requires a valid JWT

                        .anyRequest().authenticated()
                )

                // 3. Set session management to STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Add our custom JWT Filter before the standard UsernamePassword filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
