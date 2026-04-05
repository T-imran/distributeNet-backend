package com.distribnet.notifications.service;

import com.distribnet.common.exception.ResourceNotFoundException;
import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.common.repository.UserRepository;
import com.distribnet.notifications.dto.NotificationDto;
import com.distribnet.notifications.dto.NotificationPreferenceDto;
import com.distribnet.notifications.model.Notification;
import com.distribnet.notifications.model.NotificationPreference;
import com.distribnet.notifications.repository.NotificationPreferenceRepository;
import com.distribnet.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public List<NotificationDto> findAllForTenant(Tenant tenant, UUID userId, boolean unreadOnly) {
        List<Notification> notifications = unreadOnly
                ? notificationRepository.findByTenantIdAndUserIdAndReadAtIsNullOrderByCreatedAtDesc(tenant.getId(), userId)
                : notificationRepository.findByTenantIdAndUserIdOrderByCreatedAtDesc(tenant.getId(), userId);
        return notifications.stream().map(this::toDto).collect(Collectors.toList());
    }

    public NotificationDto findById(Tenant tenant, UUID userId, UUID id) {
        return notificationRepository.findByTenantIdAndUserIdAndId(tenant.getId(), userId, id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
    }

    @Transactional
    public NotificationDto save(Tenant tenant, UUID userId, Notification notification) {
        notification.setTenant(tenant);
        notification.setUser(resolveUser(userId, tenant.getId()));
        return toDto(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationDto markAsRead(Tenant tenant, UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findByTenantIdAndUserIdAndId(tenant.getId(), userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setReadAt(LocalDateTime.now());
        return toDto(notificationRepository.save(notification));
    }

    @Transactional
    public int markAllAsRead(Tenant tenant, UUID userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByTenantIdAndUserIdAndReadAtIsNullOrderByCreatedAtDesc(tenant.getId(), userId);
        unreadNotifications.forEach(notification -> notification.setReadAt(LocalDateTime.now()));
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }

    @Transactional
    public void delete(Tenant tenant, UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findByTenantIdAndUserIdAndId(tenant.getId(), userId, notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    public List<NotificationPreferenceDto> getPreferences(Tenant tenant, UUID userId) {
        return preferenceRepository.findByTenantIdAndUserId(tenant.getId(), userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<NotificationPreferenceDto> savePreferences(Tenant tenant, UUID userId, List<NotificationPreferenceDto> preferences) {
        User user = resolveUser(userId, tenant.getId());
        List<NotificationPreference> entities = preferences.stream().map(dto -> {
            NotificationPreference entity = preferenceRepository
                    .findByTenantIdAndUserIdAndType(tenant.getId(), userId, dto.getType())
                    .orElseGet(NotificationPreference::new);
            entity.setTenant(tenant);
            entity.setUser(user);
            entity.setType(dto.getType());
            entity.setEmailEnabled(Boolean.TRUE.equals(dto.getEmail()));
            entity.setPushEnabled(Boolean.TRUE.equals(dto.getPush()));
            entity.setSmsEnabled(Boolean.TRUE.equals(dto.getSms()));
            return entity;
        }).toList();
        return preferenceRepository.saveAll(entities).stream().map(this::toDto).toList();
    }

    private User resolveUser(UUID userId, UUID tenantId) {
        return userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId().toString())
                .userId(notification.getUser() != null ? notification.getUser().getId().toString() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .read(notification.getReadAt() != null)
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private NotificationPreferenceDto toDto(NotificationPreference preference) {
        NotificationPreferenceDto dto = new NotificationPreferenceDto();
        dto.setType(preference.getType());
        dto.setEmail(preference.getEmailEnabled());
        dto.setPush(preference.getPushEnabled());
        dto.setSms(preference.getSmsEnabled());
        return dto;
    }
}
