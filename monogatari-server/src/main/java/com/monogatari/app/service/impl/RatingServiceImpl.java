package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.rating.RatingRequest;
import com.monogatari.app.dto.rating.RatingResponse;
import com.monogatari.app.entity.Rating;
import com.monogatari.app.entity.Story;
import com.monogatari.app.entity.User;
import com.monogatari.app.repository.RatingRepository;
import com.monogatari.app.repository.StoryRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.RatingService;
import com.monogatari.app.service.StoryService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl extends BaseService implements RatingService {
	private final RatingRepository ratingRepository;
	
    private final StoryRepository storyRepository;
    
    private final UserService userService;
    
    private final StoryService storyService;

	@Override
	protected UserService getUserService() {
		return userService;
	}
    
    @Override
    @Transactional
    @RateLimited(maxRequests = 3, timeWindowMs = 60000) 
    public void rateStory(Long storyId, RatingRequest request) {
        User currentUser = getCurrentUser();
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        Rating rating = ratingRepository.findByUserIdAndStoryId(currentUser.getId(), storyId)
                .orElse(new Rating());

        rating.setUser(currentUser);
        rating.setStory(story);
        rating.setScore(request.getScore());
        rating.setReviewText(request.getReview());
        
        ratingRepository.save(rating);

        storyService.updateAverageRating(storyId);
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public List<RatingResponse> getStoryRatings(Long storyId) {
    	getCurrentUser();
        return ratingRepository.findByStoryId(storyId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getUserRatingForStory(Long storyId) {
        User currentUser = getCurrentUser();
        Rating rating = ratingRepository.findByUserIdAndStoryId(currentUser.getId(), storyId)
                .orElseThrow(() -> new EntityNotFoundException("You haven't rated this story yet"));
        
        return mapToResponse(rating);
    }

	private RatingResponse mapToResponse(Rating rating) {
        RatingResponse response = new RatingResponse();
        response.setId(rating.getId());
        response.setScore(rating.getScore());
        response.setReview(rating.getReviewText());
        response.setUsername(rating.getUser().getUsername());
        response.setAvatarUrl(rating.getUser().getAvatarUrl());
        response.setCreatedAt(rating.getCreatedAt());
        return response;
    }
}