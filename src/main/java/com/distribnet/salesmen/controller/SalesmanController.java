package com.distribnet.salesmen.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.salesmen.dto.SalesmanDto;
import com.distribnet.salesmen.service.SalesmanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/salesmen")
@RequiredArgsConstructor
public class SalesmanController {

    private final SalesmanService salesmanService;

    @GetMapping
    public List<SalesmanDto> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return salesmanService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesmanDto> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        SalesmanDto salesman = salesmanService.findById(tenant, id);
        return salesman != null ? ResponseEntity.ok(salesman) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<SalesmanDto> create(@Valid @RequestBody SalesmanDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return ResponseEntity.ok(salesmanService.create(tenant, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesmanDto> update(@PathVariable UUID id, @Valid @RequestBody SalesmanDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        SalesmanDto updated = salesmanService.update(tenant, id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        boolean success = salesmanService.remove(tenant, id);
        return ResponseEntity.ok(success
                ? ApiResponse.successMessage("Salesman removed")
                : ApiResponse.failure("Salesman not found", java.util.List.of()));
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
