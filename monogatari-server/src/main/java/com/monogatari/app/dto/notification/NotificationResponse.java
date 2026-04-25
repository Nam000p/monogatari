package com.monogatari.app.dto.notification;

import com.monogatari.app.enums.NotificationType;
import lombok.Data;

import java.time.Instant;

@Data
public class NotificationResponse {
	private Long id;
    private String title;
    private String message;
    private NotificationType targetType;
    private Long targetId;
    private Boolean isRead;
    private Instant createdAt;
}