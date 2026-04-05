package com.distribnet.payments.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.CustomUserDetails;
import com.distribnet.payments.dto.PaymentDto;
import com.distribnet.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> list(Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return paymentService.findAllForTenant(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        PaymentDto payment = paymentService.findById(tenant, id);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PaymentDto> create(@Valid @RequestBody PaymentDto dto, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        return ResponseEntity.ok(paymentService.create(tenant, dto));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<PaymentDto> verify(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        PaymentDto verified = paymentService.verify(tenant, id);
        return verified != null ? ResponseEntity.ok(verified) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = extractTenant(authentication);
        boolean success = paymentService.findById(tenant, id) != null;
        return ResponseEntity.ok(success
                ? ApiResponse.successMessage("Payment found")
                : ApiResponse.failure("Payment not found", java.util.List.of()));
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
