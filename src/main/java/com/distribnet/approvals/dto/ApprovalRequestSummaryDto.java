package com.distribnet.approvals.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ApprovalRequestSummaryDto {
    private UUID id;
    private String sourceType;
    private String entityType;
    private String actionType;
    private String status;
    private String summary;
    private String reviewNote;
    private UUID requestedByUserId;
    private String requestedByName;
    private UUID reviewedByUserId;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
