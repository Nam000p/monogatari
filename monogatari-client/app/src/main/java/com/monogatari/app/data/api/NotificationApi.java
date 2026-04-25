package com.monogatari.app.data.api;

import com.monogatari.app.data.model.notification.NotificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface NotificationApi {
    @GET("notifications")
    Call<List<NotificationResponse>> getNotifications();

    @PATCH("notifications/{notificationId}/read")
    Call<String> markAsRead(@Path("notificationId") Long notificationId);
}
