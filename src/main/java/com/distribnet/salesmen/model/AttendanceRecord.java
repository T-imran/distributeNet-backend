package com.distribnet.salesmen.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "attendance_record")
public class AttendanceRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesman_id", nullable = false)
    private Salesman salesman;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "check_in_at")
    private LocalDateTime checkInAt;

    @Column(name = "check_out_at")
    private LocalDateTime checkOutAt;

    @Column(name = "check_in_gps")
    private String checkInGps;

    @Column(name = "check_out_gps")
    private String checkOutGps;

    @Column(name = "visits_planned")
    private Integer visitsPlanned;

    @Column(name = "visits_completed")
    private Integer visitsCompleted = 0;

    @Column(name = "orders_placed")
    private Integer ordersPlaced = 0;

    @Column(name = "collection_amount", precision = 15, scale = 2)
    private BigDecimal collectionAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    public enum AttendanceStatus {
        PRESENT, ABSENT, PARTIAL
    }
}