package com.distribnet.users.controller;

import com.distribnet.approvals.dto.PendingActionResponseDto;
import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.common.security.SecurityUtils;
import com.distribnet.users.dto.AssignUserRoleRequest;
import com.distribnet.users.dto.CreateManagedUserRequest;
import com.distribnet.users.dto.CreateRoleRequest;
import com.distribnet.users.dto.PermissionSummaryDto;
import com.distribnet.users.dto.RoleSummaryDto;
import com.distribnet.users.dto.UpdateManagedUserRequest;
import com.distribnet.users.dto.UpdateRoleRequest;
import com.distribnet.users.dto.UserSummaryDto;
import com.distribnet.users.service.AdminUserManagementService;
import com.distribnet.users.service.AdminUserManagementProvisioningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','TENANT_ADMIN')")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;
    private final AdminUserManagementProvisioningService provisioningService;

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionSummaryDto>>> listPermissions() {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.listPermissions(),
                "Permissions fetched successfully"
        ));
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleSummaryDto>>> listRoles(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.listRoles(requireTenant(authentication)),
                "Roles fetched successfully"
        ));
    }

    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<PendingActionResponseDto>> createRole(
            @Valid @RequestBody CreateRoleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.submitCreateRole(
                        requireTenant(authentication),
                        requireCurrentUser(authentication),
                        request
                ),
                "Role request submitted successfully"
        ));
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse<PendingActionResponseDto>> updateRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRoleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.submitUpdateRole(
                        requireTenant(authentication),
                        requireCurrentUser(authentication),
                        roleId,
                        request
                ),
                "Role update request submitted successfully"
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> listUsers(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.listUsers(requireTenant(authentication)),
                "Users fetched successfully"
        ));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getUser(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.getUser(requireTenant(authentication), userId),
                "User fetched successfully"
        ));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<PendingActionResponseDto>> createUser(
            @Valid @RequestBody CreateManagedUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.submitCreateUser(
                        requireTenant(authentication),
                        requireCurrentUser(authentication),
                        request
                ),
                "User creation request submitted successfully"
        ));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<PendingActionResponseDto>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateManagedUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.submitUpdateUser(
                        requireTenant(authentication),
                        requireCurrentUser(authentication),
                        userId,
                        request
                ),
                "User update request submitted successfully"
        ));
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<PendingActionResponseDto>> assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignUserRoleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.submitAssignRole(
                        requireTenant(authentication),
                        requireCurrentUser(authentication),
                        userId,
                        request
                ),
                "User role assignment request submitted successfully"
        ));
    }

    private Tenant requireTenant(Authentication authentication) {
        return SecurityUtils.requireTenant(authentication);
    }

    private User requireCurrentUser(Authentication authentication) {
        return provisioningService.findUserEntity(requireTenant(authentication), SecurityUtils.requireUserId(authentication));
    }
}
