package com.monogatari.app.service;

import com.monogatari.app.dto.rating.RatingRequest;
import com.monogatari.app.dto.rating.RatingResponse;

import java.util.List;

public interface RatingService {
	void rateStory(Long storyId, RatingRequest request);
    
    List<RatingResponse> getStoryRatings(Long storyId);
    
    RatingResponse getUserRatingForStory(Long storyId);
}