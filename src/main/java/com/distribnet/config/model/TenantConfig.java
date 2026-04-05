package com.distribnet.config.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tenant_config")
public class TenantConfig extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    @Column(columnDefinition = "JSONB")
    private String modules;

    @Column(columnDefinition = "JSONB")
    private String regions;

    @Column(columnDefinition = "JSONB")
    private String branding;

    @Column(columnDefinition = "JSONB")
    private String workflows;

    @Column(name = "geo_location", columnDefinition = "JSONB")
    private String geoLocation;

    @Column(name = "custom_fields", columnDefinition = "JSONB")
    private String customFields;
}