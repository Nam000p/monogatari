package com.monogatari.app.dto.progress;

import lombok.Data;

import java.time.Instant;

@Data
public class ReadingProgressResponse {
	private Long storyId;
	private String storyTitle;
	private String coverUrl;
	private Long chapterId;
	private Float chapterNumber;
	private String chapterTitle;
	private Integer lastPage;
	private Instant lastReadAt;
}