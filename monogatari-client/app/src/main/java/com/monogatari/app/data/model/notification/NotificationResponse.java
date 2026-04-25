package com.monogatari.app.data.model.notification;

import java.time.LocalDateTime;

import com.monogatari.app.data.model.enums.NotificationType;

import lombok.Data;

@Data
public class NotificationResponse {
	private Long id;
    private String title;
    private String message;
    private NotificationType targetType;
    private Long targetId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}