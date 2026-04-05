package com.distribnet.notifications.repository;

import com.distribnet.common.model.Tenant;
import com.distribnet.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByTenant(Tenant tenant);
    List<Notification> findByTenantIdAndUserIdOrderByCreatedAtDesc(UUID tenantId, UUID userId);
    List<Notification> findByTenantIdAndUserIdAndReadAtIsNullOrderByCreatedAtDesc(UUID tenantId, UUID userId);
    Optional<Notification> findByTenantIdAndUserIdAndId(UUID tenantId, UUID userId, UUID id);
}
