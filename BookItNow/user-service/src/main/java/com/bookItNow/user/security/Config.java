package com.bookItNow.user.security;

import com.bookItNow.user.service.MyUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Config {

    private final MyUserDetailsService userDetailsService;

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable).cors(CorsConfigurer::disable)
                .authorizeHttpRequests(
                        requests -> requests
                        // Publicly accessible endpoints (register/login)
                        .requestMatchers("/bookitnow/v1/users/register", "/bookitnow/v1/users/login","/swagger-ui/**","/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // Admin-only endpoints
                        .requestMatchers( "/bookitnow/v1/users/delete/**","/bookitnow/v1/users/all",
                                "/bookitnow/v1/events/add", "/bookitnow/v1/events/update/**", "/bookitnow/v1/events/update/**","/bookitnow/v1/events/delete/**",
                                "/bookitnow/v1/sections/{sectionId}/capacity/{capacity}","/bookitnow/v1/sections/add/**","/bookitnow/v1/sections/delete/**","/bookitnow/v1/sections/view/**",
                                "/bookitnow/v1/seats/section/")
                        .hasRole("ADMIN")

                        // Booking management requires user authentication
                        .requestMatchers("/bookitnow/v1/users/{userId}/seats/{seatId}/select",
                                "/bookitnow/v1/bookings/new/**","/bookitnow/v1/bookings/user/**","/bookitnow/v1/bookings/delete/**").hasRole("USER")

                        // Section and seat management accessible to all authenticated users
                        .requestMatchers("/bookitnow/v1/users/view/", "/bookitnow/v1/events/venue/","/bookitnow/v1/events/upcoming","/bookitnow/v1/events/available").hasAnyRole("ADMIN", "USER")

                        // Default rule for all other endpoints
                        .anyRequest().authenticated()
                        )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            accessDeniedException.printStackTrace();
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        })
                )
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setAuthoritiesMapper(grantedAuthorities -> grantedAuthorities);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
