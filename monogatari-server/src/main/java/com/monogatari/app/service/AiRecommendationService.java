package com.monogatari.app.service;

import com.monogatari.app.dto.story.StoryResponse;

import java.util.List;

public interface AiRecommendationService {
    String generateStorySummary(Long storyId);
    
    List<StoryResponse> getRecommendedStories(Long userId);
}