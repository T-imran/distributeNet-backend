package com.distribnet.payments.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByTenant(Tenant tenant);
    Optional<Payment> findByTenantAndId(Tenant tenant, UUID id);
}
