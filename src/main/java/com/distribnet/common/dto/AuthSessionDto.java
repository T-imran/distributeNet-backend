package com.distribnet.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthSessionDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private AuthUserDto user;
    private AuthTenantDto tenant;
}
