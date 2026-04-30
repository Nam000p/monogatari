package com.monogatari.app.dto.follow;

import lombok.Data;

import java.time.Instant;

@Data
public class FollowResponse {
	private Long storyId;
	private String title;
	private String coverUrl;
	private String authorName;
	private Double averageRating;
	private Instant followedAt;
}