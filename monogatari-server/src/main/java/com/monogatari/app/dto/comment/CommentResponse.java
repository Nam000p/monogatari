package com.monogatari.app.dto.comment;

import lombok.Data;

import java.time.Instant;

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