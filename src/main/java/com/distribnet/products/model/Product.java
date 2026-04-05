package com.distribnet.products.model;

import com.distribnet.common.model.BaseEntity;
import com.distribnet.common.model.Tenant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "sku_code", nullable = false)
    private String skuCode;

    @Column(nullable = false)
    private String name;

    private String category;

    private String unit;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "trade_price", precision = 10, scale = 2)
    private BigDecimal tradePrice;

    @Column(name = "mrp", precision = 10, scale = 2)
    private BigDecimal mrp;

    @Column(name = "discount_pct", precision = 5, scale = 2)
    private BigDecimal discountPct = BigDecimal.ZERO;

    @Column(name = "stock_qty")
    private Integer stockQty = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 0;

    @Column(name = "sales_this_month")
    private Integer salesThisMonth = 0;

    @Column(name = "sales_total")
    private Integer salesTotal = 0;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    public enum ProductStatus {
        ACTIVE, INACTIVE
    }
}