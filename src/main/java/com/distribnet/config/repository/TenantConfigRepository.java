package com.distribnet.config.repository;

import com.distribnet.config.model.TenantConfig;
import com.distribnet.common.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, UUID> {
    Optional<TenantConfig> findByTenant(Tenant tenant);
}
