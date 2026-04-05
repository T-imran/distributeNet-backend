package com.distribnet.dealers.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.dealers.dto.DealerDto;
import com.distribnet.dealers.service.DealerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;

    @GetMapping
    public List<DealerDto> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return dealerService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealerDto> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        DealerDto dealer = dealerService.findById(tenant, id);
        return dealer != null ? ResponseEntity.ok(dealer) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DealerDto> create(@Valid @RequestBody DealerDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return ResponseEntity.ok(dealerService.create(tenant, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealerDto> update(@PathVariable UUID id, @Valid @RequestBody DealerDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        DealerDto updated = dealerService.update(tenant, id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        boolean success = dealerService.deactivate(tenant, id);
        return ResponseEntity.ok(success
                ? ApiResponse.successMessage("Dealer deactivated")
                : ApiResponse.failure("Dealer not found", java.util.List.of()));
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
