package com.distribnet.notifications.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationPreferenceDto {
    @NotBlank
    private String type;
    private Boolean email;
    private Boolean push;
    private Boolean sms;
}
