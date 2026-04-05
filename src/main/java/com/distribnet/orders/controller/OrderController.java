package com.distribnet.orders.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.orders.dto.OrderDto;
import com.distribnet.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return orderService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        OrderDto order = orderService.findById(tenant, id);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return ResponseEntity.ok(orderService.create(tenant, dto));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<OrderDto> approve(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        OrderDto approved = orderService.approve(tenant, id);
        return approved != null ? ResponseEntity.ok(approved) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        boolean success = orderService.cancel(tenant, id);
        return ResponseEntity.ok(success
                ? ApiResponse.successMessage("Order cancelled")
                : ApiResponse.failure("Order not found", java.util.List.of()));
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
