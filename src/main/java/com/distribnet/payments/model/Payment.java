package com.distribnet.payments.model;

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

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "payment_number", nullable = false)
    private String paymentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retailer_id", nullable = false)
    private Retailer retailer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salesman_id")
    private Salesman salesman;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    private String method;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ChequeDetails chequeDetails;

    public enum PaymentStatus {
        PENDING, VERIFIED, REJECTED
    }
}