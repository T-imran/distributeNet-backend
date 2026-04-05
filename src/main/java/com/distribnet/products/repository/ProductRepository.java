package com.distribnet.products.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByTenant(Tenant tenant);
    Optional<Product> findByTenantAndId(Tenant tenant, UUID id);
}
