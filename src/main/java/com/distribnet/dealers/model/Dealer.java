package com.distribnet.dealers.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dealer")
public class Dealer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_name")
    private String ownerName;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private DealerStatus status = DealerStatus.ACTIVE;

    private String division;

    private String district;

    private String thana;

    private String territory;

    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(name = "total_sales", precision = 15, scale = 2)
    private BigDecimal totalSales = BigDecimal.ZERO;

    @Column(name = "joined_at")
    private LocalDate joinedAt;

    public enum DealerStatus {
        ACTIVE, INACTIVE
    }
}