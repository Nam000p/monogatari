package com.monogatari.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoryNotificationService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    
    public void broadcastNewChapter(Long storyId, String action, Object data) {
        String destination = "/topic/stories/" + storyId;
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("action", action);
        payload.put("data", data);
        
        simpMessagingTemplate.convertAndSend(destination, payload);
    }

    public void sendPrivateNotification(Long userId, String action, Object data) {
        String destination = "/topic/notifications/" + userId;
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", action);
        payload.put("data", data);
        
        simpMessagingTemplate.convertAndSend(destination, payload);
    }
}