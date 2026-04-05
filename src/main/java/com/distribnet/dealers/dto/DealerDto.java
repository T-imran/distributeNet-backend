package com.distribnet.dealers.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DealerDto {
    private String id;
    private String code;
    private String name;
    private String ownerName;
    private String email;
    private String phone;
    private String status;
    private String division;
    private String district;
    private String thana;
    private String territory;
    private BigDecimal creditLimit;
    private BigDecimal outstandingBalance;
    private BigDecimal totalSales;
    private LocalDate joinedAt;
}
