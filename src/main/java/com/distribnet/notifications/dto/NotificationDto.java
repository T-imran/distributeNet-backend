package com.distribnet.notifications.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private String id;
    private String userId;
    private String title;
    private String message;
    private String type;
    private String priority;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
