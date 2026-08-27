package com.worktrack.security.tenant;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TenantAspect {

    @Before("execution(* com.worktrack.serviceImpl.*.*(..))")
    public void validateTenantContext(JoinPoint joinPoint) {
        Long currentTenant = TenantContext.getCurrentTenant();
        if (currentTenant != null) {
            log.trace("Executing service method {} under tenant context {}", joinPoint.getSignature().toShortString(), currentTenant);
        }
    }
}
