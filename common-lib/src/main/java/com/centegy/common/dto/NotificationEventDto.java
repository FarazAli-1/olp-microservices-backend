package com.centegy.common.dto;

import com.centegy.common.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDto {
    private String recipientId;
    private String recipientEmail;
    private NotificationType notificationType;
    private String message;
}