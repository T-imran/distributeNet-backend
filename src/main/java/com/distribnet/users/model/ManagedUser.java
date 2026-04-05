package com.distribnet.users.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "managed_user")
public class ManagedUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private ManagedUserStatus status = ManagedUserStatus.ACTIVE;

    private String region;

    public enum UserRole {
        SUPER_ADMIN, TENANT_ADMIN, MANAGER, SALESMAN, DEALER, RETAILER
    }

    public enum ManagedUserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}