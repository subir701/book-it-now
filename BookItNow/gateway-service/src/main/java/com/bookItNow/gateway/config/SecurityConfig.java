package com.bookItNow.gateway.config;

import com.bookItNow.gateway.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges

                        // Public
                        .pathMatchers("/bookitnow/v1/users/register",
                                "/bookitnow/v1/users/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()

                        // Admin
                        .pathMatchers("/bookitnow/v1/users/delete/**",
                                "/bookitnow/v1/users/all",
                                "/bookitnow/v1/events/add",
                                "/bookitnow/v1/events/update/**",
                                "/bookitnow/v1/events/delete/**",
                                "/bookitnow/v1/sections/**").hasRole("ADMIN")

                        // User
                        .pathMatchers("/bookitnow/v1/users/{userId}/seats/{seatId}/select",
                                "/bookitnow/v1/bookings/**").hasRole("USER")

                        // Common
                        .pathMatchers("/bookitnow/v1/events/**",
                                "/bookitnow/v1/users/view/**").hasAnyRole("USER", "ADMIN")

                        // Any other
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
