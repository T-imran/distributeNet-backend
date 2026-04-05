package com.distribnet.common.security;

import com.distribnet.common.model.Tenant;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static CustomUserDetails requireUserDetails(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new IllegalStateException("Authenticated tenant context is required");
        }
        return details;
    }

    public static Tenant requireTenant(Authentication authentication) {
        CustomUserDetails details = requireUserDetails(authentication);
        Tenant tenant = new Tenant();
        tenant.setId(UUID.fromString(details.getTenantId()));
        tenant.setDomain(details.getTenantDomain());
        return tenant;
    }

    public static UUID requireUserId(Authentication authentication) {
        return UUID.fromString(requireUserDetails(authentication).getId());
    }
}
