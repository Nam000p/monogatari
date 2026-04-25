package com.monogatari.app.service;

import com.monogatari.app.dto.story.StoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoryService {
	Page<StoryResponse> getAllStories(Pageable pageable);
	
    Page<StoryResponse> searchStories(String query, Pageable pageable);
    
    Page<StoryResponse> getStoriesByGenre(Long genreId, Pageable pageable);
    
    StoryResponse getStoryById(Long id);
    
    void updateViewCount(Long storyId);
    
    void updateAverageRating(Long storyId);
    
    Page<StoryResponse> getLatestStories(Pageable pageable);
    
    Page<StoryResponse> getTopViewedStories(Pageable pageable);
    
    Page<StoryResponse> getTopRatedStories(Pageable pageable);
}
