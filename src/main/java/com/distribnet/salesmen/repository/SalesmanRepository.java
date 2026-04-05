package com.distribnet.salesmen.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.salesmen.model.Salesman;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SalesmanRepository extends JpaRepository<Salesman, UUID> {
    List<Salesman> findByTenant(Tenant tenant);
    Optional<Salesman> findByTenantAndId(Tenant tenant, UUID id);
}
