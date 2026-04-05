package com.distribnet.config.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.SecurityUtils;
import com.distribnet.config.model.TenantConfig;
import com.distribnet.config.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/configuration")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping
    public ResponseEntity<ApiResponse<TenantConfig>> getConfig(Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        return ResponseEntity.ok(ApiResponse.success(configService.getConfig(tenant), "Tenant configuration fetched"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TenantConfig>> saveConfig(@RequestBody TenantConfig config, Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        return ResponseEntity.ok(ApiResponse.success(configService.saveConfig(tenant, config), "Tenant configuration updated"));
    }
}
