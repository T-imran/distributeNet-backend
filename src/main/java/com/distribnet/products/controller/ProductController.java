package com.distribnet.products.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.products.dto.ProductDto;
import com.distribnet.products.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return productService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        ProductDto product = productService.findById(tenant, id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return ResponseEntity.ok(productService.create(tenant, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @Valid @RequestBody ProductDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        ProductDto updated = productService.update(tenant, id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        boolean success = productService.remove(tenant, id);
        return ResponseEntity.ok(success
                ? ApiResponse.successMessage("Product removed")
                : ApiResponse.failure("Product not found", java.util.List.of()));
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
