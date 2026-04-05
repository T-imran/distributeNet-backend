package com.distribnet.orders.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.dealers.model.Dealer;
import com.distribnet.retailers.model.Retailer;
import com.distribnet.salesmen.model.Salesman;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "\"order\"")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesman_id")
    private Salesman salesman;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_id", nullable = false)
    private Retailer retailer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 15, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    public enum OrderStatus {
        SUBMITTED, APPROVED, PROCESSING, DISPATCHED, DELIVERED, COMPLETED, CANCELLED, RETURNED
    }
}