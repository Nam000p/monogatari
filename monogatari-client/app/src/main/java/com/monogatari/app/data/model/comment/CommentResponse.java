package com.monogatari.app.data.model.comment;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponse {
	private Long id;
	private String content;
	private Long userId;
	private String username;
	private String avatarUrl;
	private Long chapterId;
	private LocalDateTime createdAt;
}