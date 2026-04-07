package com.distribnet.users.repository;

import com.distribnet.users.model.RoleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleDefinitionRepository extends JpaRepository<RoleDefinition, UUID> {
    List<RoleDefinition> findByTenantIdOrderByNameAsc(UUID tenantId);
    Optional<RoleDefinition> findByIdAndTenantId(UUID id, UUID tenantId);
    boolean existsByTenantIdAndCodeIgnoreCase(UUID tenantId, String code);
    boolean existsByTenantIdAndCodeIgnoreCaseAndIdNot(UUID tenantId, String code, UUID id);
}
