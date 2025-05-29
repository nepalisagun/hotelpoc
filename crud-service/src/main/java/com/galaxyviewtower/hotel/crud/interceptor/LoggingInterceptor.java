package com.galaxyviewtower.hotel.crud.interceptor;

import com.galaxyviewtower.hotel.crud.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class LoggingInterceptor implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String USER_ID_HEADER = "X-User-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
            exchange.getRequest().mutate().header(REQUEST_ID_HEADER, requestId);
        }

        String userId = exchange.getRequest().getHeaders().getFirst(USER_ID_HEADER);
        if (userId == null) {
            userId = "anonymous";
        }

        LoggingUtil.setServiceName("crud-service");
        LoggingUtil.setEnvironment(exchange.getEnvironment().getActiveProfiles()[0]);

        logger.info("Incoming request: {} {}", 
            exchange.getRequest().getMethod(),
            exchange.getRequest().getURI());

        return chain.filter(exchange)
            .doFinally(signalType -> {
                logger.info("Completed request: {} {} with status {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getURI(),
                    exchange.getResponse().getStatusCode());
                LoggingUtil.clearContext();
            });
    }
} 