package com.monogatari.app.data.model.progress;

import java.time.Instant;

import lombok.Data;

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