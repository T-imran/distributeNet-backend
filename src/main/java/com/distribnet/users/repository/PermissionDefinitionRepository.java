package com.distribnet.users.repository;

import com.distribnet.users.model.PermissionDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PermissionDefinitionRepository extends JpaRepository<PermissionDefinition, UUID> {
    List<PermissionDefinition> findAllByOrderByModuleNameAscNameAsc();
    List<PermissionDefinition> findByIdIn(Collection<UUID> ids);
}
