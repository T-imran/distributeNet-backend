package com.distribnet.retailers.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RetailerDto {
    private String id;
    private String code;
    private String name;
    private String ownerName;
    private String phone;
    private String email;
    private String address;
    private String tier;
    private String businessType;
    private BigDecimal outstandingBalance;
    private BigDecimal totalPurchases;
    private Integer visitCount;
    private Integer orderCount;
    private String region;
    private String gps;
    private String status;
    private String dealerId;
}
