package com.distribnet.notifications.controller;

import com.distribnet.common.dto.ApiResponse;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.security.SecurityUtils;
import com.distribnet.notifications.dto.NotificationDto;
import com.distribnet.notifications.dto.NotificationPreferenceDto;
import com.distribnet.notifications.model.Notification;
import com.distribnet.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.findAllForTenant(tenant, userId, unreadOnly),
                "Notifications fetched"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationDto>> get(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.findById(tenant, userId, id),
                "Notification fetched"
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationDto>> save(@RequestBody Notification notification, Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.save(tenant, userId, notification),
                "Notification created"
        ));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markRead(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.markAsRead(tenant, userId, id),
                "Notification marked as read"
        ));
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllRead(Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.markAllAsRead(tenant, userId),
                "Unread notifications marked as read"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        notificationService.delete(tenant, userId, id);
        return ResponseEntity.ok(ApiResponse.successMessage("Notification deleted"));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<List<NotificationPreferenceDto>>> preferences(Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getPreferences(tenant, userId),
                "Notification preferences fetched"
        ));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<List<NotificationPreferenceDto>>> updatePreferences(
            @Valid @RequestBody List<NotificationPreferenceDto> preferences,
            Authentication authentication) {
        Tenant tenant = SecurityUtils.requireTenant(authentication);
        UUID userId = SecurityUtils.requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.savePreferences(tenant, userId, preferences),
                "Notification preferences updated"
        ));
    }
}
