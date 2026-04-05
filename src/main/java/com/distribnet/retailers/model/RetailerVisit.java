package com.distribnet.retailers.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import com.distribnet.salesmen.model.Salesman;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "retailer_visit")
public class RetailerVisit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_id", nullable = false)
    private Retailer retailer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesman_id", nullable = false)
    private Salesman salesman;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    private String outcome;

    @Column(name = "order_amount", precision = 15, scale = 2)
    private BigDecimal orderAmount;

    private String gps;
}