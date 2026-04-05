package com.distribnet.retailers.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.retailers.model.Retailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RetailerRepository extends JpaRepository<Retailer, UUID> {
    List<Retailer> findByTenant(Tenant tenant);
    Optional<Retailer> findByTenantAndId(Tenant tenant, UUID id);
}
