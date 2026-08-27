package com.worktrack.security.rbac;

import com.worktrack.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return false;
        }

        String targetPermission = permission.toString();
        log.trace("Checking permission '{}' for user '{}'", targetPermission, user.getEmail());

        // Admin has full access
        if (user.getRole() != null && user.getRole().name().equalsIgnoreCase("ADMIN")) {
            return true;
        }

        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, null, permission);
    }
}
