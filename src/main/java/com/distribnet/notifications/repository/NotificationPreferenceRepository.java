package com.distribnet.notifications.repository;

import com.distribnet.notifications.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {
    List<NotificationPreference> findByTenantIdAndUserId(UUID tenantId, UUID userId);
    Optional<NotificationPreference> findByTenantIdAndUserIdAndType(UUID tenantId, UUID userId, String type);
}
