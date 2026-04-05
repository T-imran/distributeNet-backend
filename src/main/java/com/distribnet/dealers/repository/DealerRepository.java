package com.distribnet.dealers.repository;

import com.distribnet.dealers.model.Dealer;
import com.distribnet.common.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealerRepository extends JpaRepository<Dealer, UUID> {
    List<Dealer> findByTenant(Tenant tenant);
    Optional<Dealer> findByTenantAndId(Tenant tenant, UUID id);
}
