package com.galaxyviewtower.hotel.crud.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
public class LoggingConfig {

    @Bean
    public WebFilter loggingFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            String requestId = UUID.randomUUID().toString();
            String traceId = exchange.getRequest().getHeaders().getFirst("X-B3-TraceId");
            String spanId = exchange.getRequest().getHeaders().getFirst("X-B3-SpanId");
            String parentId = exchange.getRequest().getHeaders().getFirst("X-B3-ParentSpanId");
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String sessionId = exchange.getRequest().getHeaders().getFirst("X-Session-Id");
            String clientIp = exchange.getRequest().getRemoteAddress() != null ? 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
            String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");
            String requestMethod = exchange.getRequest().getMethod().name();
            String requestUri = exchange.getRequest().getURI().toString();

            return chain.filter(exchange)
                .doFirst(() -> {
                    MDC.put("requestId", requestId);
                    MDC.put("traceId", traceId != null ? traceId : requestId);
                    MDC.put("spanId", spanId != null ? spanId : requestId);
                    MDC.put("parentId", parentId);
                    MDC.put("userId", userId);
                    MDC.put("sessionId", sessionId);
                    MDC.put("clientIp", clientIp);
                    MDC.put("userAgent", userAgent);
                    MDC.put("requestMethod", requestMethod);
                    MDC.put("requestUri", requestUri);
                })
                .doFinally(signalType -> {
                    String responseStatus = String.valueOf(exchange.getResponse().getStatusCode().value());
                    MDC.put("responseStatus", responseStatus);
                    
                    // Calculate response time
                    long startTime = System.currentTimeMillis();
                    long responseTime = System.currentTimeMillis() - startTime;
                    MDC.put("responseTime", String.valueOf(responseTime));

                    // Clear MDC
                    MDC.clear();
                });
        };
    }
} 