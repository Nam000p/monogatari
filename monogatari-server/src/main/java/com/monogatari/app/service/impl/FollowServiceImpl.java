package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.follow.FollowResponse;
import com.monogatari.app.entity.Follow;
import com.monogatari.app.entity.FollowId;
import com.monogatari.app.entity.Story;
import com.monogatari.app.entity.User;
import com.monogatari.app.repository.FollowRepository;
import com.monogatari.app.repository.StoryRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.FollowService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends BaseService implements FollowService {
	private final FollowRepository followRepository;
	
	private final StoryRepository storyRepository;
	
	private final UserService userService;
	
	@Override
	protected UserService getUserService() {
		return userService;
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 10, timeWindowMs = 60000)
	public String toggleFollow(Long storyId) {
		User currentUser = getCurrentUser();
		Story story = storyRepository.findById(storyId)
				.orElseThrow(() -> new EntityNotFoundException("Story not found!"));
		
		FollowId id = new FollowId(currentUser.getId(), story.getId());
		return followRepository.findById(id)
				.map(follow -> {
					followRepository.delete(follow);
					return "Unfollowed successfully";
				}).orElseGet(() -> {
					Follow follow = Follow.builder()
							.id(id)
							.user(currentUser)
							.story(story)
							.build();
					followRepository.save(follow);
					return "Followed successfully!";
				});
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public List<FollowResponse> getMyFollowedStories() {
		User currentUser = getCurrentUser();
        return followRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isFollowing(Long storyId) {
		User currentUser = getCurrentUser();
		return followRepository.existsByUserIdAndStoryId(currentUser.getId(), storyId);
	}

	@Override
	@Transactional(readOnly = true)
	public long getFollowCount(Long storyId) {
		return followRepository.countByStoryId(storyId);
	}
	
	private FollowResponse mapToResponse(Follow follow) {
        FollowResponse response = new FollowResponse();    
        response.setStoryId(follow.getStory().getId());
        response.setTitle(follow.getStory().getTitle());
        response.setCoverUrl(follow.getStory().getCoverUrl());
        response.setAuthorName(follow.getStory().getAuthor() != null ? follow.getStory().getAuthor().getName() : "Unknown");
        response.setAverageRating(follow.getStory().getAverageRating());
        response.setFollowedAt(follow.getCreatedAt());
        
        return response;
    }
}