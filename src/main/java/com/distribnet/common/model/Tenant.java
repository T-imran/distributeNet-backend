package com.distribnet.common.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenant")
public class Tenant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String domain;

    private String plan;

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "app_name")
    private String appName;

    @Enumerated(EnumType.STRING)
    private TenantStatus status = TenantStatus.ACTIVE;

    public enum TenantStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}