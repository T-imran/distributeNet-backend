package com.distribnet.orders.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByTenant(Tenant tenant);
    Optional<Order> findByTenantAndId(Tenant tenant, UUID id);
}
