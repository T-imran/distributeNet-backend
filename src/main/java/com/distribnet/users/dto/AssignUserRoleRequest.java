package com.distribnet.users.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AssignUserRoleRequest {
    @NotNull
    private UUID roleId;
}
