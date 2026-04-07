package com.distribnet.approvals.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApprovalDecisionRequest {
    @Size(max = 500)
    private String reviewNote;
}
