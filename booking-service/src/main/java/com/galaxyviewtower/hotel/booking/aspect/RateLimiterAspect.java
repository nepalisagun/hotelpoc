package com.galaxyviewtower.hotel.booking.aspect;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RateLimiterRegistry rateLimiterRegistry;

    @Around("@annotation(com.galaxyviewtower.hotel.booking.annotation.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(methodName);

        if (joinPoint.getArgs()[0] instanceof Mono) {
            return ((Mono<?>) joinPoint.getArgs()[0])
                    .flatMap(result -> {
                        if (rateLimiter.acquirePermission()) {
                            return Mono.just(result);
                        } else {
                            return Mono.error(new RuntimeException("Rate limit exceeded"));
                        }
                    });
        }

        if (rateLimiter.acquirePermission()) {
            return joinPoint.proceed();
        } else {
            throw new RuntimeException("Rate limit exceeded");
        }
    }
} 