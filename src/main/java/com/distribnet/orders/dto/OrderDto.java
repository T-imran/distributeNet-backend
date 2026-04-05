package com.distribnet.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto {
    private String id;
    private String orderNumber;
    private String status;
    private String salesmanId;
    private String retailerId;
    private String dealerId;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String approvedBy;
    private List<OrderItemDto> items;
}
