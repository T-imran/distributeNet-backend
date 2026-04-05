package com.distribnet.users.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.users.model.ManagedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ManagedUserRepository extends JpaRepository<ManagedUser, UUID> {
    List<ManagedUser> findByTenant(Tenant tenant);
}
