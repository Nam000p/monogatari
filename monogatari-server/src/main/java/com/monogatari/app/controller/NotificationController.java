package com.monogatari.app.controller;

import com.monogatari.app.dto.notification.NotificationResponse;
import com.monogatari.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
    	List<NotificationResponse> responses = notificationService.getUserNotifications();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return new ResponseEntity<>("Already read!", HttpStatus.OK);
    }
}