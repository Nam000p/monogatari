package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.NotificationApi;
import com.monogatari.app.data.model.notification.NotificationResponse;

import java.util.List;

import retrofit2.Call;

public class NotificationRepository {
    private final NotificationApi notificationApi;

    public NotificationRepository(NotificationApi notificationApi) {
        this.notificationApi = notificationApi;
    }

    public Call<List<NotificationResponse>> getNotifications() {
        return this.notificationApi.getNotifications();
    }

    public Call<String> markAsRead(Long notificationId) {
        return this.notificationApi.markAsRead(notificationId);
    }
}