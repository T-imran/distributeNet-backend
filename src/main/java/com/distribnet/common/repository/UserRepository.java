package com.distribnet.common.repository;

import com.distribnet.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<User> findByTenantIdAndEmailIgnoreCase(UUID tenantId, String email);
    boolean existsByTenantIdAndEmailIgnoreCase(UUID tenantId, String email);
    List<User> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    List<User> findByTenantIdAndRoleDefinitionId(UUID tenantId, UUID roleDefinitionId);
}
