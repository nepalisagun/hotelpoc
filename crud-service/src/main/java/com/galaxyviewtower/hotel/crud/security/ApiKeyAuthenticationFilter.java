package com.galaxyviewtower.hotel.crud.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class ApiKeyAuthenticationFilter implements WebFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private final ApiKeyRotationService apiKeyRotationService;

    public ApiKeyAuthenticationFilter(ApiKeyRotationService apiKeyRotationService) {
        this.apiKeyRotationService = apiKeyRotationService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
        
        if (apiKey == null) {
            return chain.filter(exchange);
        }

        return apiKeyRotationService.validateApiKey(apiKey)
            .flatMap(isValid -> {
                if (isValid) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        "api-client",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_CLIENT"))
                    );
                    return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                }
                return chain.filter(exchange);
            });
    }
} 