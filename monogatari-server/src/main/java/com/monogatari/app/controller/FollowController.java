package com.monogatari.app.controller;

import com.monogatari.app.dto.follow.FollowResponse;
import com.monogatari.app.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FollowController {
	private final FollowService followService;
	
	@PostMapping("/stories/{storyId}/follow")
	public ResponseEntity<String> toggleFollow(@PathVariable Long storyId) {
		return new ResponseEntity<>(followService.toggleFollow(storyId), HttpStatus.OK);
	}
	
	@GetMapping("/stories/{storyId}/follow/check")
	public ResponseEntity<Boolean> checkFollowStatus(@PathVariable Long storyId) {
		return new ResponseEntity<>(followService.isFollowing(storyId), HttpStatus.OK);
	}
	
	@GetMapping("/me/follows")
	public ResponseEntity<List<FollowResponse>> getMyFollow() {
		List<FollowResponse> responses = followService.getMyFollowedStories();
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}
} 