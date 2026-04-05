package com.distribnet.dashboard.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.SecurityUtils;
import com.distribnet.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> kpis(Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getKpis(tenant), "Dashboard KPIs fetched"));
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> recentOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "8") int limit) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentOrders(tenant, limit), "Recent orders fetched"));
    }

    @GetMapping("/top-salesmen")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topSalesmen(
            Authentication authentication,
            @RequestParam(defaultValue = "6") int limit) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getTopSalesmen(tenant, limit), "Top salesmen fetched"));
    }

    @GetMapping("/order-status-breakdown")
    public ResponseEntity<ApiResponse<Map<String, Long>>> orderStatusBreakdown(Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getOrderStatusBreakdown(tenant),
                "Order status breakdown fetched"
        ));
    }
}
