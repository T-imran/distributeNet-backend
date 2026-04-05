package com.distribnet.common.repository;

import com.distribnet.common.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByIdAndRevokedAtIsNull(UUID id);
    Optional<UserSession> findByIdAndRevokedAtIsNullAndExpiresAtAfter(UUID id, LocalDateTime now);
}
