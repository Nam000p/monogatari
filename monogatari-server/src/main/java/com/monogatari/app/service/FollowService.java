package com.monogatari.app.service;

import com.monogatari.app.dto.follow.FollowResponse;

import java.util.List;

public interface FollowService {
	String toggleFollow(Long storyId);
    
    List<FollowResponse> getMyFollowedStories();
    
    boolean isFollowing(Long storyId);
    
    long getFollowCount(Long storyId);
}