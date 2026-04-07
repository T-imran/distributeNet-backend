package com.distribnet.users.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSummaryDto {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String region;
    private String status;
    private String baseRole;
    private UUID roleId;
    private String roleCode;
    private String roleName;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
