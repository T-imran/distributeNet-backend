package com.distribnet.users.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
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
    public ResponseEntity<ApiResponse<RoleSummaryDto>> createRole(
            @Valid @RequestBody CreateRoleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.createRole(requireTenant(authentication), request),
                "Role created successfully"
        ));
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<ApiResponse<RoleSummaryDto>> updateRole(
            @PathVariable UUID roleId,
            @Valid @RequestBody UpdateRoleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.updateRole(requireTenant(authentication), roleId, request),
                "Role updated successfully"
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
    public ResponseEntity<ApiResponse<UserSummaryDto>> createUser(
            @Valid @RequestBody CreateManagedUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.createUser(requireTenant(authentication), request),
                "User created successfully"
        ));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserSummaryDto>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateManagedUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.updateUser(requireTenant(authentication), userId, request),
                "User updated successfully"
        ));
    }

    @PatchMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserSummaryDto>> assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignUserRoleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminUserManagementService.assignRole(requireTenant(authentication), userId, request),
                "User role assigned successfully"
        ));
    }

    private Tenant requireTenant(Authentication authentication) {
        return SecurityUtils.requireTenant(authentication);
    }
}
