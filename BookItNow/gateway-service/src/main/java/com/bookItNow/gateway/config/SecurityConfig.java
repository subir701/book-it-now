package com.bookItNow.gateway.config;

import com.bookItNow.gateway.security.JwtAuthenticationFilter;
import com.bookItNow.gateway.service.JWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Date;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final JWTService jwtService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ReactiveStringRedisTemplate redisTemplate, JWTService jwtService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;

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
                                "/bookitnow/v1/users/view/**","bookitnow/v1/seats/selected","bookitnow/v1/payements/pay").hasAnyRole("USER", "ADMIN")

                        // Any other
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                // --- LOGOUT CONFIGURATION --
                .logout(logout -> logout
                        .logoutUrl("/bookitnow/v1/users/logout")
                        .logoutHandler(((exchange, authentication) -> {
                            String authHeader = exchange.getExchange().getRequest().getHeaders().getFirst("Authorization");

                            if(authHeader != null && authHeader.startsWith("Bearer ")){
                                String token = authHeader.substring(7);
                                try{
                                    String jti = jwtService.extractJti(token);
                                    Date expiration = jwtService.extractExpriation(token);

                                    long ttl = (expiration.getTime() - System.currentTimeMillis()) / 100;

                                    if (ttl > 0) {
                                        // Save to Redis Blacklist
                                        return redisTemplate.opsForValue().set(
                                                "blacklist:" + jti,
                                                "revoked",
                                                java.time.Duration.ofSeconds(ttl)
                                        ).then();
                                    }
                                } catch (Exception e) {
                                    // Token might be malformed, just ignore
                                }
                            }
                            return reactor.core.publisher.Mono.empty();


                        })))
                .build();
    }
}
