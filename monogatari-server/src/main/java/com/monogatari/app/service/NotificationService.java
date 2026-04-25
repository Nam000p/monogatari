package com.monogatari.app.service;

import com.monogatari.app.dto.notification.NotificationResponse;

import java.util.List;

public interface NotificationService {
	List<NotificationResponse> getUserNotifications();
	
    void markAsRead(Long notificationId);
    
    void createNewChapterNotification(Long storyId, Long chapterId);
}
