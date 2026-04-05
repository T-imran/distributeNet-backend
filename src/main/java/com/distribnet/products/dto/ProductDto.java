package com.distribnet.products.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private String id;
    private String skuCode;
    private String name;
    private String category;
    private String unit;
    private BigDecimal basePrice;
    private BigDecimal tradePrice;
    private BigDecimal mrp;
    private BigDecimal discountPct;
    private Integer stockQty;
    private Integer reorderLevel;
    private Integer salesThisMonth;
    private Integer salesTotal;
    private String status;
}
