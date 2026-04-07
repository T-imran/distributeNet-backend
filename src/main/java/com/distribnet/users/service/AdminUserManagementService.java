package com.distribnet.users.service;

import com.distribnet.common.exception.BadRequestException;
import com.distribnet.common.exception.ResourceNotFoundException;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.common.repository.UserRepository;
import com.distribnet.users.dto.AssignUserRoleRequest;
import com.distribnet.users.dto.CreateManagedUserRequest;
import com.distribnet.users.dto.CreateRoleRequest;
import com.distribnet.users.dto.PermissionSummaryDto;
import com.distribnet.users.dto.RoleSummaryDto;
import com.distribnet.users.dto.UpdateManagedUserRequest;
import com.distribnet.users.dto.UpdateRoleRequest;
import com.distribnet.users.dto.UserSummaryDto;
import com.distribnet.users.model.ManagedUser;
import com.distribnet.users.model.PermissionDefinition;
import com.distribnet.users.model.RoleDefinition;
import com.distribnet.users.repository.ManagedUserRepository;
import com.distribnet.users.repository.PermissionDefinitionRepository;
import com.distribnet.users.repository.RoleDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserManagementService {

    private final RoleDefinitionRepository roleDefinitionRepository;
    private final PermissionDefinitionRepository permissionDefinitionRepository;
    private final UserRepository userRepository;
    private final ManagedUserRepository managedUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<PermissionSummaryDto> listPermissions() {
        return permissionDefinitionRepository.findAllByOrderByModuleNameAscNameAsc().stream()
                .map(this::toPermissionDto)
                .toList();
    }

    public List<RoleSummaryDto> listRoles(Tenant tenant) {
        Map<UUID, PermissionDefinition> permissionMap = permissionDefinitionRepository.findAll().stream()
                .collect(Collectors.toMap(PermissionDefinition::getId, Function.identity()));
        return roleDefinitionRepository.findByTenantIdOrderByNameAsc(tenant.getId()).stream()
                .map(role -> toRoleDto(role, permissionMap))
                .toList();
    }

    @Transactional
    public RoleSummaryDto createRole(Tenant tenant, CreateRoleRequest request) {
        String normalizedCode = normalizeCode(request.getCode());
        if (roleDefinitionRepository.existsByTenantIdAndCodeIgnoreCase(tenant.getId(), normalizedCode)) {
            throw new BadRequestException("Role code already exists for this tenant");
        }

        RoleDefinition roleDefinition = new RoleDefinition();
        roleDefinition.setTenant(tenant);
        roleDefinition.setCode(normalizedCode);
        roleDefinition.setName(request.getName().trim());
        roleDefinition.setDescription(trimToNull(request.getDescription()));
        roleDefinition.setBaseRole(parseBaseRole(request.getBaseRole()));
        roleDefinition.setActive(Boolean.TRUE.equals(request.getActive()));
        roleDefinition.setPermissionIds(resolvePermissionIds(request.getPermissionIds()));

        RoleDefinition saved = roleDefinitionRepository.save(roleDefinition);
        return toRoleDto(saved, loadPermissionMap(saved.getPermissionIds()));
    }

    @Transactional
    public RoleSummaryDto updateRole(Tenant tenant, UUID roleId, UpdateRoleRequest request) {
        RoleDefinition roleDefinition = roleDefinitionRepository.findByIdAndTenantId(roleId, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        roleDefinition.setName(request.getName().trim());
        roleDefinition.setDescription(trimToNull(request.getDescription()));
        roleDefinition.setBaseRole(parseBaseRole(request.getBaseRole()));
        roleDefinition.setActive(Boolean.TRUE.equals(request.getActive()));
        roleDefinition.setPermissionIds(resolvePermissionIds(request.getPermissionIds()));

        RoleDefinition saved = roleDefinitionRepository.save(roleDefinition);
        syncUsersForRoleChange(saved);
        return toRoleDto(saved, loadPermissionMap(saved.getPermissionIds()));
    }

    public List<UserSummaryDto> listUsers(Tenant tenant) {
        return userRepository.findByTenantIdOrderByCreatedAtDesc(tenant.getId()).stream()
                .map(this::toUserDto)
                .toList();
    }

    public UserSummaryDto getUser(Tenant tenant, UUID userId) {
        return toUserDto(findUser(tenant, userId));
    }

    @Transactional
    public UserSummaryDto createUser(Tenant tenant, CreateManagedUserRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByTenantIdAndEmailIgnoreCase(tenant.getId(), normalizedEmail)) {
            throw new BadRequestException("A user with this email already exists for this tenant");
        }

        RoleDefinition roleDefinition = requireActiveRole(tenant, request.getRoleId());

        User user = new User();
        user.setTenant(tenant);
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(trimToNull(request.getPhone()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleDefinition.getBaseRole());
        user.setRoleDefinition(roleDefinition);
        user.setStatus(parseStatus(request.getStatus()));
        user.setRegion(trimToNull(request.getRegion()));

        User saved = userRepository.save(user);
        upsertManagedUser(saved);
        return toUserDto(saved);
    }

    @Transactional
    public UserSummaryDto updateUser(Tenant tenant, UUID userId, UpdateManagedUserRequest request) {
        User user = findUser(tenant, userId);
        RoleDefinition roleDefinition = requireActiveRole(tenant, request.getRoleId());
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

        userRepository.findByTenantIdAndEmailIgnoreCase(tenant.getId(), normalizedEmail)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new BadRequestException("A user with this email already exists for this tenant");
                });

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPhone(trimToNull(request.getPhone()));
        user.setRegion(trimToNull(request.getRegion()));
        user.setStatus(parseStatus(request.getStatus()));
        user.setRole(roleDefinition.getBaseRole());
        user.setRoleDefinition(roleDefinition);

        User saved = userRepository.save(user);
        upsertManagedUser(saved);
        return toUserDto(saved);
    }

    @Transactional
    public UserSummaryDto assignRole(Tenant tenant, UUID userId, AssignUserRoleRequest request) {
        User user = findUser(tenant, userId);
        RoleDefinition roleDefinition = requireActiveRole(tenant, request.getRoleId());
        user.setRole(roleDefinition.getBaseRole());
        user.setRoleDefinition(roleDefinition);
        User saved = userRepository.save(user);
        upsertManagedUser(saved);
        return toUserDto(saved);
    }

    private User findUser(Tenant tenant, UUID userId) {
        return userRepository.findByIdAndTenantId(userId, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private RoleDefinition requireActiveRole(Tenant tenant, UUID roleId) {
        RoleDefinition roleDefinition = roleDefinitionRepository.findByIdAndTenantId(roleId, tenant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        if (!roleDefinition.isActive()) {
            throw new BadRequestException("Inactive roles cannot be assigned");
        }
        return roleDefinition;
    }

    private Set<UUID> resolvePermissionIds(Set<UUID> permissionIds) {
        List<PermissionDefinition> permissions = permissionDefinitionRepository.findByIdIn(permissionIds);
        if (permissions.size() != permissionIds.size()) {
            throw new BadRequestException("One or more permissions were not found");
        }
        return permissions.stream()
                .map(PermissionDefinition::getId)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    private Map<UUID, PermissionDefinition> loadPermissionMap(Collection<UUID> permissionIds) {
        if (permissionIds.isEmpty()) {
            return Map.of();
        }
        return permissionDefinitionRepository.findByIdIn(permissionIds).stream()
                .collect(Collectors.toMap(PermissionDefinition::getId, Function.identity()));
    }

    private RoleSummaryDto toRoleDto(RoleDefinition roleDefinition, Map<UUID, PermissionDefinition> permissionMap) {
        List<PermissionSummaryDto> permissions = roleDefinition.getPermissionIds().stream()
                .map(permissionMap::get)
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparing(PermissionDefinition::getModuleName).thenComparing(PermissionDefinition::getName))
                .map(this::toPermissionDto)
                .toList();

        return RoleSummaryDto.builder()
                .id(roleDefinition.getId())
                .code(roleDefinition.getCode())
                .name(roleDefinition.getName())
                .description(roleDefinition.getDescription())
                .baseRole(roleDefinition.getBaseRole().name())
                .active(roleDefinition.isActive())
                .permissions(permissions)
                .createdAt(roleDefinition.getCreatedAt())
                .updatedAt(roleDefinition.getUpdatedAt())
                .build();
    }

    private PermissionSummaryDto toPermissionDto(PermissionDefinition permission) {
        return PermissionSummaryDto.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .name(permission.getName())
                .module(permission.getModuleName())
                .description(permission.getDescription())
                .build();
    }

    private UserSummaryDto toUserDto(User user) {
        RoleDefinition roleDefinition = user.getRoleDefinition();
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .region(user.getRegion())
                .status(user.getStatus().name())
                .baseRole(user.getRole().name())
                .roleId(roleDefinition != null ? roleDefinition.getId() : null)
                .roleCode(roleDefinition != null ? roleDefinition.getCode() : null)
                .roleName(roleDefinition != null ? roleDefinition.getName() : null)
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User.Role parseBaseRole(String baseRole) {
        try {
            return User.Role.valueOf(baseRole.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid baseRole value");
        }
    }

    private User.UserStatus parseStatus(String status) {
        try {
            return User.UserStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid status value");
        }
    }

    private String normalizeCode(String code) {
        return code.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void syncUsersForRoleChange(RoleDefinition roleDefinition) {
        List<User> users = userRepository.findByTenantIdAndRoleDefinitionId(roleDefinition.getTenant().getId(), roleDefinition.getId());
        if (users.isEmpty()) {
            return;
        }
        for (User user : users) {
            user.setRole(roleDefinition.getBaseRole());
            upsertManagedUser(user);
        }
        userRepository.saveAll(users);
    }

    private void upsertManagedUser(User user) {
        ManagedUser managedUser = managedUserRepository.findByUserId(user.getId()).orElseGet(ManagedUser::new);
        managedUser.setTenant(user.getTenant());
        managedUser.setUser(user);
        managedUser.setName(user.getName());
        managedUser.setEmail(user.getEmail());
        managedUser.setRole(ManagedUser.UserRole.valueOf(user.getRole().name()));
        managedUser.setStatus(ManagedUser.ManagedUserStatus.valueOf(user.getStatus().name()));
        managedUser.setRegion(user.getRegion());
        managedUserRepository.save(managedUser);
    }
}
