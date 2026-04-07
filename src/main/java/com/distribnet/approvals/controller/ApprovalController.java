package com.distribnet.approvals.controller;

import com.distribnet.approvals.dto.ApprovalDecisionRequest;
import com.distribnet.approvals.dto.ApprovalRequestSummaryDto;
import com.distribnet.approvals.dto.CustomerApprovalRequestCreateDto;
import com.distribnet.approvals.service.ApprovalWorkflowService;
import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.common.security.SecurityUtils;
import com.distribnet.users.service.AdminUserManagementProvisioningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','TENANT_ADMIN')")
public class ApprovalController {

    private final ApprovalWorkflowService approvalWorkflowService;
    private final AdminUserManagementProvisioningService provisioningService;

    @GetMapping("/admin-requests")
    public ResponseEntity<ApiResponse<List<ApprovalRequestSummaryDto>>> listAdminRequests(
            @RequestParam(required = false) String status,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalWorkflowService.listAdminRequests(requireTenant(authentication), status),
                "Admin approval requests fetched successfully"
        ));
    }

    @GetMapping("/customer-requests")
    public ResponseEntity<ApiResponse<List<ApprovalRequestSummaryDto>>> listCustomerRequests(
            @RequestParam(required = false) String status,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalWorkflowService.listCustomerRequests(requireTenant(authentication), status),
                "Customer approval requests fetched successfully"
        ));
    }

    @PostMapping("/customer-requests")
    public ResponseEntity<ApiResponse<ApprovalRequestSummaryDto>> createCustomerRequest(
            @Valid @RequestBody CustomerApprovalRequestCreateDto request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalWorkflowService.submitCustomerRequest(requireTenant(authentication), requireCurrentUser(authentication), request),
                "Customer request submitted for approval"
        ));
    }

    @PostMapping("/{approvalRequestId}/approve")
    public ResponseEntity<ApiResponse<ApprovalRequestSummaryDto>> approve(
            @PathVariable UUID approvalRequestId,
            @Valid @RequestBody ApprovalDecisionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalWorkflowService.approve(requireTenant(authentication), approvalRequestId, requireCurrentUser(authentication), request),
                "Approval completed successfully"
        ));
    }

    @PostMapping("/{approvalRequestId}/reject")
    public ResponseEntity<ApiResponse<ApprovalRequestSummaryDto>> reject(
            @PathVariable UUID approvalRequestId,
            @Valid @RequestBody ApprovalDecisionRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalWorkflowService.reject(requireTenant(authentication), approvalRequestId, requireCurrentUser(authentication), request),
                "Request rejected successfully"
        ));
    }

    private Tenant requireTenant(Authentication authentication) {
        return SecurityUtils.requireTenant(authentication);
    }

    private User requireCurrentUser(Authentication authentication) {
        return provisioningService.findUserEntity(requireTenant(authentication), SecurityUtils.requireUserId(authentication));
    }
}
