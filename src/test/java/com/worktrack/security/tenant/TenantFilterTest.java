package com.worktrack.security.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest {

    private TenantFilter tenantFilter;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        tenantFilter = new TenantFilter();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should extract X-Tenant-ID header and set TenantContext during filter execution")
    void doFilterInternal_ExtractsHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(TenantFilter.TENANT_HEADER, "100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        org.mockito.Mockito.doAnswer(invocation -> {
            assertThat(TenantContext.getCurrentTenant()).isEqualTo(100L);
            return null;
        }).when(filterChain).doFilter(any(), any());

        tenantFilter.doFilterInternal(request, response, filterChain);

        // Verify cleared in finally block
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}
