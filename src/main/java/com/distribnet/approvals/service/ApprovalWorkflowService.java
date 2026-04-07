package com.distribnet.approvals.service;

import com.distribnet.approvals.dto.ApprovalDecisionRequest;
import com.distribnet.approvals.dto.ApprovalRequestSummaryDto;
import com.distribnet.approvals.dto.CustomerApprovalRequestCreateDto;
import com.distribnet.approvals.dto.PendingActionResponseDto;
import com.distribnet.approvals.model.ApprovalRequest;
import com.distribnet.approvals.repository.ApprovalRequestRepository;
import com.distribnet.common.exception.BadRequestException;
import com.distribnet.common.exception.ResourceNotFoundException;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.users.dto.AssignUserRoleRequest;
import com.distribnet.users.dto.CreateManagedUserRequest;
import com.distribnet.users.dto.CreateRoleRequest;
import com.distribnet.users.dto.UpdateManagedUserRequest;
import com.distribnet.users.dto.UpdateRoleRequest;
import com.distribnet.users.service.AdminUserManagementProvisioningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalWorkflowService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final AdminUserManagementProvisioningService provisioningService;
    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    public List<ApprovalRequestSummaryDto> listAdminRequests(Tenant tenant, String status) {
        return listBySource(tenant, ApprovalRequest.SourceType.ADMIN_PORTAL, status);
    }

    public List<ApprovalRequestSummaryDto> listCustomerRequests(Tenant tenant, String status) {
        return listBySource(tenant, ApprovalRequest.SourceType.CUSTOMER_PORTAL, status);
    }

    @Transactional
    public PendingActionResponseDto submitAdminRoleCreate(Tenant tenant, User requestedBy, CreateRoleRequest request) {
        ApprovalRequest approvalRequest = createApproval(
                tenant,
                requestedBy,
                ApprovalRequest.SourceType.ADMIN_PORTAL,
                ApprovalRequest.EntityType.ROLE,
                ApprovalRequest.ActionType.CREATE,
                "Create role: " + request.getName().trim(),
                request
        );
        return pendingResponse("Role creation submitted for approval", approvalRequest);
    }

    @Transactional
    public PendingActionResponseDto submitAdminRoleUpdate(Tenant tenant, User requestedBy, UUID roleId, UpdateRoleRequest request) {
        ApprovalRequest approvalRequest = createApproval(
                tenant,
                requestedBy,
                ApprovalRequest.SourceType.ADMIN_PORTAL,
                ApprovalRequest.EntityType.ROLE,
                ApprovalRequest.ActionType.UPDATE,
                "Update role: " + request.getName().trim(),
                Map.of("roleId", roleId, "request", request)
        );
        return pendingResponse("Role update submitted for approval", approvalRequest);
    }

    @Transactional
    public PendingActionResponseDto submitAdminUserCreate(Tenant tenant, User requestedBy, CreateManagedUserRequest request) {
        ApprovalRequest approvalRequest = createApproval(
                tenant,
                requestedBy,
                ApprovalRequest.SourceType.ADMIN_PORTAL,
                ApprovalRequest.EntityType.USER,
                ApprovalRequest.ActionType.CREATE,
                "Create user: " + request.getName().trim(),
                request
        );
        return pendingResponse("User creation submitted for approval", approvalRequest);
    }

    @Transactional
    public PendingActionResponseDto submitAdminUserUpdate(Tenant tenant, User requestedBy, UUID userId, UpdateManagedUserRequest request) {
        ApprovalRequest approvalRequest = createApproval(
                tenant,
                requestedBy,
                ApprovalRequest.SourceType.ADMIN_PORTAL,
                ApprovalRequest.EntityType.USER,
                ApprovalRequest.ActionType.UPDATE,
                "Update user: " + request.getName().trim(),
                Map.of("userId", userId, "request", request)
        );
        return pendingResponse("User update submitted for approval", approvalRequest);
    }

    @Transactional
    public PendingActionResponseDto submitAdminUserRoleAssignment(Tenant tenant, User requestedBy, UUID userId, AssignUserRoleRequest request) {
        User targetUser = provisioningService.findUserEntity(tenant, userId);
        ApprovalRequest approvalRequest = createApproval(
                tenant,
                requestedBy,
                ApprovalRequest.SourceType.ADMIN_PORTAL,
                ApprovalRequest.EntityType.USER,
                ApprovalRequest.ActionType.ASSIGN_ROLE,
                "Assign role for user: " + targetUser.getName(),
                Map.of("userId", userId, "request", request)
        );
        return pendingResponse("User role assignment submitted for approval", approvalRequest);
    }

    @Transactional
    public ApprovalRequestSummaryDto submitCustomerRequest(Tenant tenant, User requestedBy, CustomerApprovalRequestCreateDto request) {
        ApprovalRequest approvalRequest = createApproval(
                tenant,
                requestedBy,
                ApprovalRequest.SourceType.CUSTOMER_PORTAL,
                ApprovalRequest.EntityType.CUSTOMER_REQUEST,
                ApprovalRequest.ActionType.REQUEST,
                request.getSummary().trim(),
                request
        );
        return toSummary(approvalRequest);
    }

    @Transactional
    public ApprovalRequestSummaryDto approve(Tenant tenant, UUID approvalRequestId, User reviewer, ApprovalDecisionRequest request) {
        ApprovalRequest approvalRequest = findPendingApproval(tenant, approvalRequestId);
        if (approvalRequest.getRequestedBy() != null && approvalRequest.getRequestedBy().getId().equals(reviewer.getId())) {
            throw new BadRequestException("Requester cannot approve their own request");
        }

        applyApproval(tenant, approvalRequest);
        approvalRequest.setStatus(ApprovalRequest.ApprovalStatus.APPROVED);
        approvalRequest.setReviewedBy(reviewer);
        approvalRequest.setReviewedAt(LocalDateTime.now());
        approvalRequest.setReviewNote(trimToNull(request.getReviewNote()));
        return toSummary(approvalRequestRepository.save(approvalRequest));
    }

    @Transactional
    public ApprovalRequestSummaryDto reject(Tenant tenant, UUID approvalRequestId, User reviewer, ApprovalDecisionRequest request) {
        ApprovalRequest approvalRequest = findPendingApproval(tenant, approvalRequestId);
        if (approvalRequest.getRequestedBy() != null && approvalRequest.getRequestedBy().getId().equals(reviewer.getId())) {
            throw new BadRequestException("Requester cannot reject their own request");
        }

        approvalRequest.setStatus(ApprovalRequest.ApprovalStatus.REJECTED);
        approvalRequest.setReviewedBy(reviewer);
        approvalRequest.setReviewedAt(LocalDateTime.now());
        approvalRequest.setReviewNote(trimToNull(request.getReviewNote()));
        return toSummary(approvalRequestRepository.save(approvalRequest));
    }

    private List<ApprovalRequestSummaryDto> listBySource(Tenant tenant, ApprovalRequest.SourceType sourceType, String status) {
        List<ApprovalRequest> requests = status == null || status.isBlank()
                ? approvalRequestRepository.findByTenantIdAndSourceTypeOrderByCreatedAtDesc(tenant.getId(), sourceType)
                : approvalRequestRepository.findByTenantIdAndSourceTypeAndStatusOrderByCreatedAtDesc(
                        tenant.getId(),
                        sourceType,
                        parseStatus(status)
                );
        return requests.stream().map(this::toSummary).toList();
    }

    private ApprovalRequest createApproval(
            Tenant tenant,
            User requestedBy,
            ApprovalRequest.SourceType sourceType,
            ApprovalRequest.EntityType entityType,
            ApprovalRequest.ActionType actionType,
            String summary,
            Object payload
    ) {
        ApprovalRequest approvalRequest = new ApprovalRequest();
        approvalRequest.setTenant(tenant);
        approvalRequest.setRequestedBy(requestedBy);
        approvalRequest.setSourceType(sourceType);
        approvalRequest.setEntityType(entityType);
        approvalRequest.setActionType(actionType);
        approvalRequest.setSummary(summary);
        approvalRequest.setPayload(writePayload(payload));
        return approvalRequestRepository.save(approvalRequest);
    }

    private PendingActionResponseDto pendingResponse(String message, ApprovalRequest approvalRequest) {
        return PendingActionResponseDto.builder()
                .message(message)
                .approvalRequest(toSummary(approvalRequest))
                .build();
    }

    private ApprovalRequest findPendingApproval(Tenant tenant, UUID approvalRequestId) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findByIdAndTenantId(approvalRequestId, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Approval request not found"));
        if (approvalRequest.getStatus() != ApprovalRequest.ApprovalStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be reviewed");
        }
        return approvalRequest;
    }

    private void applyApproval(Tenant tenant, ApprovalRequest approvalRequest) {
        try {
            if (approvalRequest.getSourceType() == ApprovalRequest.SourceType.CUSTOMER_PORTAL) {
                return;
            }

            if (approvalRequest.getEntityType() == ApprovalRequest.EntityType.ROLE
                    && approvalRequest.getActionType() == ApprovalRequest.ActionType.CREATE) {
                provisioningService.createRole(tenant, readPayload(approvalRequest.getPayload(), CreateRoleRequest.class));
                return;
            }

            if (approvalRequest.getEntityType() == ApprovalRequest.EntityType.ROLE
                    && approvalRequest.getActionType() == ApprovalRequest.ActionType.UPDATE) {
                Map<String, Object> payload = readPayloadMap(approvalRequest.getPayload());
                UUID roleId = UUID.fromString(String.valueOf(payload.get("roleId")));
                UpdateRoleRequest request = objectMapper.convertValue(payload.get("request"), UpdateRoleRequest.class);
                provisioningService.updateRole(tenant, roleId, request);
                return;
            }

            if (approvalRequest.getEntityType() == ApprovalRequest.EntityType.USER
                    && approvalRequest.getActionType() == ApprovalRequest.ActionType.CREATE) {
                provisioningService.createUser(tenant, readPayload(approvalRequest.getPayload(), CreateManagedUserRequest.class));
                return;
            }

            if (approvalRequest.getEntityType() == ApprovalRequest.EntityType.USER
                    && approvalRequest.getActionType() == ApprovalRequest.ActionType.UPDATE) {
                Map<String, Object> payload = readPayloadMap(approvalRequest.getPayload());
                UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
                UpdateManagedUserRequest request = objectMapper.convertValue(payload.get("request"), UpdateManagedUserRequest.class);
                provisioningService.updateUser(tenant, userId, request);
                return;
            }

            if (approvalRequest.getEntityType() == ApprovalRequest.EntityType.USER
                    && approvalRequest.getActionType() == ApprovalRequest.ActionType.ASSIGN_ROLE) {
                Map<String, Object> payload = readPayloadMap(approvalRequest.getPayload());
                UUID userId = UUID.fromString(String.valueOf(payload.get("userId")));
                AssignUserRoleRequest request = objectMapper.convertValue(payload.get("request"), AssignUserRoleRequest.class);
                provisioningService.assignRole(tenant, userId, request);
                return;
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Approval payload is invalid");
        }

        throw new BadRequestException("Unsupported approval action");
    }

    private String writePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to store approval payload", ex);
        }
    }

    private <T> T readPayload(String payload, Class<T> targetType) {
        try {
            return objectMapper.readValue(payload, targetType);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Approval payload is invalid");
        }
    }

    private Map<String, Object> readPayloadMap(String payload) {
        try {
            return objectMapper.readValue(payload, new TypeReference<>() { });
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Approval payload is invalid");
        }
    }

    private ApprovalRequest.ApprovalStatus parseStatus(String status) {
        try {
            return ApprovalRequest.ApprovalStatus.valueOf(status.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid approval status value");
        }
    }

    private ApprovalRequestSummaryDto toSummary(ApprovalRequest request) {
        return ApprovalRequestSummaryDto.builder()
                .id(request.getId())
                .sourceType(request.getSourceType().name())
                .entityType(request.getEntityType().name())
                .actionType(request.getActionType().name())
                .status(request.getStatus().name())
                .summary(request.getSummary())
                .reviewNote(request.getReviewNote())
                .requestedByUserId(request.getRequestedBy() != null ? request.getRequestedBy().getId() : null)
                .requestedByName(request.getRequestedBy() != null ? request.getRequestedBy().getName() : null)
                .reviewedByUserId(request.getReviewedBy() != null ? request.getReviewedBy().getId() : null)
                .reviewedByName(request.getReviewedBy() != null ? request.getReviewedBy().getName() : null)
                .reviewedAt(request.getReviewedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
