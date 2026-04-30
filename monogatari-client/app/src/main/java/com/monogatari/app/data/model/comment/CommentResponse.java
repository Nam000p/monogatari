package com.monogatari.app.data.model.comment;

import java.time.Instant;

import lombok.Data;

@Data
public class CommentResponse {
	private Long id;
	private String content;
	private Long userId;
	private String username;
	private String avatarUrl;
	private Long chapterId;
	private Instant createdAt;
}