package com.distribnet.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthUserDto {
    private String id;
    private String tenantId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
    private String region;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
