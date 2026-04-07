package com.distribnet.users.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class RoleSummaryDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String baseRole;
    private boolean active;
    private List<PermissionSummaryDto> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
