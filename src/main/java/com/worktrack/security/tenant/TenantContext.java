package com.worktrack.security.tenant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static Long getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(Long tenantId) {
        log.trace("Setting current tenant ID to {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static void clear() {
        log.trace("Clearing current tenant ID");
        CURRENT_TENANT.remove();
    }
}
