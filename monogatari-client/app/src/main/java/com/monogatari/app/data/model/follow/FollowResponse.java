package com.monogatari.app.data.model.follow;

import java.time.Instant;

import lombok.Data;

@Data
public class FollowResponse {
	private Long storyId;
	private String title;
	private String coverUrl;
	private String authorName;
	private Double averageRating;
	private Instant followedAt;
}