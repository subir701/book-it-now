package com.bookItNow.gateway.security;

import com.bookItNow.gateway.service.JWTService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthenticationFilter implements WebFilter {

    private final JWTService jwtService;
    private final ReactiveStringRedisTemplate redisTemplate;

    public JwtAuthenticationFilter(JWTService jwtService, ReactiveStringRedisTemplate redisTemplate) {
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 1. Extract JTI (Serial Number) from Token
            String jti;
            try {
                jti = jwtService.extractJti(token);
            } catch (Exception e) {
                return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            // 2. Non-blocking check against Redis Blacklist
            return redisTemplate.hasKey("blacklist:" + jti)
                    .flatMap(isBlacklisted -> {
                        if (Boolean.TRUE.equals(isBlacklisted)) {
                            return onError(exchange, "Token Revoked", HttpStatus.UNAUTHORIZED);
                        }

                        // 3. Token is clean, proceed with Authentication logic
                        try {
                            String username = jwtService.extractUserName(token);
                            String role = jwtService.extractRole(token);

                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(username, null,
                                            Collections.singletonList(new SimpleGrantedAuthority(role)));

                            // 4. Set Security Context and continue the chain
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                            Mono.just(new SecurityContextImpl(auth))));

                        } catch (Exception e) {
                            return onError(exchange, "Authentication Failed", HttpStatus.UNAUTHORIZED);
                        }
                    });
        }

        return chain.filter(exchange);
    }

    // Helper method to handle errors reactively
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}