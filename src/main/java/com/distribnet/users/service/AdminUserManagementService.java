package com.distribnet.users.service;

import com.distribnet.approvals.dto.PendingActionResponseDto;
import com.distribnet.approvals.service.ApprovalWorkflowService;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.users.dto.AssignUserRoleRequest;
import com.distribnet.users.dto.CreateManagedUserRequest;
import com.distribnet.users.dto.CreateRoleRequest;
import com.distribnet.users.dto.PermissionSummaryDto;
import com.distribnet.users.dto.RoleSummaryDto;
import com.distribnet.users.dto.UpdateManagedUserRequest;
import com.distribnet.users.dto.UpdateRoleRequest;
import com.distribnet.users.dto.UserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserManagementService {

    private final AdminUserManagementProvisioningService provisioningService;
    private final ApprovalWorkflowService approvalWorkflowService;

    public List<PermissionSummaryDto> listPermissions() {
        return provisioningService.listPermissions();
    }

    public List<RoleSummaryDto> listRoles(Tenant tenant) {
        return provisioningService.listRoles(tenant);
    }

    public List<UserSummaryDto> listUsers(Tenant tenant) {
        return provisioningService.listUsers(tenant);
    }

    public UserSummaryDto getUser(Tenant tenant, UUID userId) {
        return provisioningService.getUser(tenant, userId);
    }

    @Transactional
    public PendingActionResponseDto submitCreateRole(Tenant tenant, User requestedBy, CreateRoleRequest request) {
        return approvalWorkflowService.submitAdminRoleCreate(tenant, requestedBy, request);
    }

    @Transactional
    public PendingActionResponseDto submitUpdateRole(Tenant tenant, User requestedBy, UUID roleId, UpdateRoleRequest request) {
        return approvalWorkflowService.submitAdminRoleUpdate(tenant, requestedBy, roleId, request);
    }

    @Transactional
    public PendingActionResponseDto submitCreateUser(Tenant tenant, User requestedBy, CreateManagedUserRequest request) {
        return approvalWorkflowService.submitAdminUserCreate(tenant, requestedBy, request);
    }

    @Transactional
    public PendingActionResponseDto submitUpdateUser(Tenant tenant, User requestedBy, UUID userId, UpdateManagedUserRequest request) {
        return approvalWorkflowService.submitAdminUserUpdate(tenant, requestedBy, userId, request);
    }

    @Transactional
    public PendingActionResponseDto submitAssignRole(Tenant tenant, User requestedBy, UUID userId, AssignUserRoleRequest request) {
        return approvalWorkflowService.submitAdminUserRoleAssignment(tenant, requestedBy, userId, request);
    }
}
