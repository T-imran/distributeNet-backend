package com.distribnet.payments.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private String id;
    private String paymentNumber;
    private String retailerId;
    private String dealerId;
    private String salesmanId;
    private BigDecimal amount;
    private String method;
    private String status;
    private LocalDateTime receivedAt;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
}
