package com.monogatari.app.service;

import com.monogatari.app.dto.chapter.ChapterRequest;
import com.monogatari.app.dto.chapter.ChapterResponse;

import java.util.List;

public interface ChapterService {
	List<ChapterResponse> getChaptersByStoryId(Long storyId);
	
    ChapterResponse getChapterDetails(Long chapterId);
    
    ChapterResponse createChapter(Long storyId, ChapterRequest request);
}
