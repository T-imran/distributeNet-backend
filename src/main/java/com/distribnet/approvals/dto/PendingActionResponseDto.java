package com.distribnet.approvals.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingActionResponseDto {
    private String message;
    private ApprovalRequestSummaryDto approvalRequest;
}
