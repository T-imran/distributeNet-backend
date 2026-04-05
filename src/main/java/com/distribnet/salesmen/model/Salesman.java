package com.distribnet.salesmen.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "salesman")
public class Salesman extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String region;

    private String division;

    private String district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(name = "monthly_target", precision = 15, scale = 2)
    private BigDecimal monthlyTarget;

    @Column(name = "monthly_achieved", precision = 15, scale = 2)
    private BigDecimal monthlyAchieved = BigDecimal.ZERO;

    @Column(name = "achievement_pct", precision = 5, scale = 2)
    private BigDecimal achievementPct = BigDecimal.ZERO;

    @Column(name = "visit_target")
    private Integer visitTarget;

    @Column(name = "visits_completed")
    private Integer visitsCompleted = 0;

    @Column(name = "orders_this_month")
    private Integer ordersThisMonth = 0;

    @Column(name = "collection_this_month", precision = 15, scale = 2)
    private BigDecimal collectionThisMonth = BigDecimal.ZERO;

    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private BigDecimal attendanceRate = BigDecimal.ZERO;

    @Column(name = "current_status")
    @Enumerated(EnumType.STRING)
    private SalesmanStatus currentStatus = SalesmanStatus.ACTIVE;

    public enum SalesmanStatus {
        ACTIVE, INACTIVE
    }
}