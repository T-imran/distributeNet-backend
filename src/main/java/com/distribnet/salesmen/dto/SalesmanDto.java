package com.distribnet.salesmen.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesmanDto {
    private String id;
    private String userId;
    private String region;
    private String division;
    private String district;
    private String managerId;
    private BigDecimal monthlyTarget;
    private BigDecimal monthlyAchieved;
    private BigDecimal achievementPct;
    private Integer visitTarget;
    private Integer visitsCompleted;
    private Integer ordersThisMonth;
    private BigDecimal collectionThisMonth;
    private BigDecimal attendanceRate;
    private String currentStatus;
}
