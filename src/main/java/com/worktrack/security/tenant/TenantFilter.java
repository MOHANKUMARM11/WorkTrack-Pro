package com.worktrack.security.tenant;

import com.worktrack.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            Long tenantId = null;

            // 1. Try resolving from X-Tenant-ID header
            String tenantHeader = request.getHeader(TENANT_HEADER);
            if (tenantHeader != null && !tenantHeader.isBlank()) {
                try {
                    tenantId = Long.parseLong(tenantHeader.trim());
                } catch (NumberFormatException e) {
                    log.warn("Invalid X-Tenant-ID header value: {}", tenantHeader);
                }
            }

            // 2. Fallback to authenticated user's company ID
            if (tenantId == null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof User user) {
                    if (user.getCompany() != null) {
                        tenantId = user.getCompany().getId();
                    }
                }
            }

            if (tenantId != null) {
                TenantContext.setCurrentTenant(tenantId);
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
