package com.worktrack.security.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should set, get, and clear tenant ID in ThreadLocal context")
    void tenantContext_SetGetClear() {
        TenantContext.setCurrentTenant(100L);
        assertThat(TenantContext.getCurrentTenant()).isEqualTo(100L);

        TenantContext.clear();
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}
