package com.distribnet.payments.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cheque_details")
public class ChequeDetails extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "cheque_number", nullable = false)
    private String chequeNumber;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    @Enumerated(EnumType.STRING)
    private ChequeStatus status = ChequeStatus.DEPOSITED;

    @Column(name = "bounce_reason", columnDefinition = "TEXT")
    private String bounceReason;

    public enum ChequeStatus {
        DEPOSITED, CLEARED, BOUNCED
    }
}