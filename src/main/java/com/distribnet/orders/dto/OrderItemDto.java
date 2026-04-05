package com.distribnet.orders.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private String skuId;
    private String skuCode;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal total;
}
