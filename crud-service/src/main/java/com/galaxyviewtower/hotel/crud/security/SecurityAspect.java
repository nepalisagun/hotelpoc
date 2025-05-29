package com.galaxyviewtower.hotel.crud.security;

import com.galaxyviewtower.hotel.crud.exception.JwtAuthenticationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Aspect
@Component
public class SecurityAspect {

    @Around("@annotation(requiresAdmin)")
    public Object checkAdminRole(ProceedingJoinPoint joinPoint, RequiresAdmin requiresAdmin) {
        return ReactiveSecurityContextHolder.isAdmin()
                .flatMap(isAdmin -> {
                    if (isAdmin) {
                        try {
                            return (Mono<?>) joinPoint.proceed();
                        } catch (Throwable e) {
                            return Mono.error(e);
                        }
                    }
                    return Mono.error(new JwtAuthenticationException("Access denied. Admin role required."));
                });
    }

    @Around("@annotation(requiresStaff)")
    public Object checkStaffRole(ProceedingJoinPoint joinPoint, RequiresStaff requiresStaff) {
        return ReactiveSecurityContextHolder.isStaff()
                .flatMap(isStaff -> {
                    if (isStaff) {
                        try {
                            return (Mono<?>) joinPoint.proceed();
                        } catch (Throwable e) {
                            return Mono.error(e);
                        }
                    }
                    return Mono.error(new JwtAuthenticationException("Access denied. Staff role required."));
                });
    }

    @Around("@annotation(requiresCustomer)")
    public Object checkCustomerRole(ProceedingJoinPoint joinPoint, RequiresCustomer requiresCustomer) {
        return ReactiveSecurityContextHolder.isCustomer()
                .flatMap(isCustomer -> {
                    if (isCustomer) {
                        try {
                            return (Mono<?>) joinPoint.proceed();
                        } catch (Throwable e) {
                            return Mono.error(e);
                        }
                    }
                    return Mono.error(new JwtAuthenticationException("Access denied. Customer role required."));
                });
    }
} 