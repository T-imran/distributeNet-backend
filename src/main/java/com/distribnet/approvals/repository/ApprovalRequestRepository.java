package com.distribnet.approvals.repository;

import com.distribnet.approvals.model.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, UUID> {
    List<ApprovalRequest> findByTenantIdAndSourceTypeOrderByCreatedAtDesc(UUID tenantId, ApprovalRequest.SourceType sourceType);
    List<ApprovalRequest> findByTenantIdAndSourceTypeAndStatusOrderByCreatedAtDesc(
            UUID tenantId,
            ApprovalRequest.SourceType sourceType,
            ApprovalRequest.ApprovalStatus status
    );
    Optional<ApprovalRequest> findByIdAndTenantId(UUID id, UUID tenantId);
}
