package com.distribnet.payments.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ledger_entry")
public class LedgerEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_type", nullable = false)
    private String entityType; // dealer or retailer

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String type; // debit or credit

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_number")
    private String referenceNumber;
}