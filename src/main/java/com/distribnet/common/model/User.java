package com.distribnet.common.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "\"user\"")
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private String region;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public enum Role {
        SUPER_ADMIN, TENANT_ADMIN, MANAGER, SALESMAN, DEALER, RETAILER
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }

    // Permissions can be derived from role or stored separately
    // For now, we'll use role-based
}