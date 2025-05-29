package com.galaxyviewtower.hotel.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf().disable()
            .authorizeExchange()
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/api/users/register").permitAll()
                .pathMatchers("/api/users/verify-email/**").permitAll()
                .pathMatchers("/api/users/reset-password/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            .and()
            .httpBasic().disable()
            .formLogin().disable();
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 