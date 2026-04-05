package com.distribnet.retailers.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import com.distribnet.dealers.model.Dealer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "retailer")
public class Retailer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "owner_name")
    private String ownerName;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String tier;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "outstanding_balance", precision = 15, scale = 2)
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @Column(name = "total_purchases", precision = 15, scale = 2)
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    @Column(name = "visit_count")
    private Integer visitCount = 0;

    @Column(name = "order_count")
    private Integer orderCount = 0;

    private String region;

    private String gps;

    @Enumerated(EnumType.STRING)
    private RetailerStatus status = RetailerStatus.ACTIVE;

    public enum RetailerStatus {
        ACTIVE, INACTIVE
    }
}