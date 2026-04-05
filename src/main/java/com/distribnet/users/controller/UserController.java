package com.distribnet.users.controller;

import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.users.model.ManagedUser;
import com.distribnet.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<ManagedUser> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return userService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManagedUser> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        ManagedUser user = userService.findById(tenant, id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ManagedUser> save(@RequestBody ManagedUser user, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        user.setTenant(tenant);
        return ResponseEntity.ok(userService.save(user));
    }

    private Tenant extractTenant(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails details)) {
            throw new IllegalStateException("Authenticated tenant context is required");
        }
        Tenant tenant = new Tenant();
        tenant.setId(java.util.UUID.fromString(details.getTenantId()));
        tenant.setDomain(details.getTenantDomain());
        return tenant;
    }
}
