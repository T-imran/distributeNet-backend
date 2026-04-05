package com.distribnet.payments.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "aging_bucket")
public class AgingBucket extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String label;

    @Column(name = "min_days")
    private Integer minDays;

    @Column(name = "max_days")
    private Integer maxDays;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "dealer_count")
    private Integer dealerCount = 0;
}