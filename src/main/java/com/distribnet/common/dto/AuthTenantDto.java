package com.distribnet.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthTenantDto {
    private String id;
    private String name;
    private String domain;
    private String plan;
    private String primaryColor;
    private String appName;
}
