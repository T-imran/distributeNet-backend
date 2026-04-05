package com.distribnet.retailers.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.retailers.dto.RetailerDto;
import com.distribnet.retailers.service.RetailerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/retailers")
@RequiredArgsConstructor
public class RetailerController {

    private final RetailerService retailerService;

    @GetMapping
    public List<RetailerDto> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return retailerService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RetailerDto> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        RetailerDto retailer = retailerService.findById(tenant, id);
        return retailer != null ? ResponseEntity.ok(retailer) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<RetailerDto> create(@Valid @RequestBody RetailerDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return ResponseEntity.ok(retailerService.create(tenant, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RetailerDto> update(@PathVariable UUID id, @Valid @RequestBody RetailerDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        RetailerDto updated = retailerService.update(tenant, id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        boolean success = retailerService.delete(tenant, id);
        return ResponseEntity.ok(success
                ? ApiResponse.successMessage("Retailer deleted")
                : ApiResponse.failure("Retailer not found", java.util.List.of()));
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
