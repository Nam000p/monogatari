package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.progress.ReadingProgressRequest;
import com.monogatari.app.dto.progress.ReadingProgressResponse;
import com.monogatari.app.entity.Chapter;
import com.monogatari.app.entity.ReadingProgress;
import com.monogatari.app.entity.Story;
import com.monogatari.app.entity.User;
import com.monogatari.app.repository.ChapterRepository;
import com.monogatari.app.repository.ReadingProgressRepository;
import com.monogatari.app.repository.StoryRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.ReadingProgressService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReadingProgressServiceImpl extends BaseService implements ReadingProgressService {
	private final ReadingProgressRepository progressRepository;
	
	private final StoryRepository storyRepository;
	
	private final ChapterRepository chapterRepository;
	
	private final UserService userService;
	
	@Override
	protected UserService getUserService() {
		return userService;
	}

	@Override
	@Transactional
	@RateLimited(maxRequests = 20, timeWindowMs = 60000)
	public void updateProgress(Long storyId, ReadingProgressRequest request) {
		if (!storyId.equals(request.getStoryId())) {
	        throw new IllegalArgumentException("Story ID mismatch! URL ID: " + storyId + 
	                                         ", but Request Body ID: " + request.getStoryId());
	    }
		
	    User user = getCurrentUser();
	    
	    Story story = storyRepository.findById(storyId)
	            .orElseThrow(() -> new EntityNotFoundException("Story not found with ID: " + storyId));
	    
	    Chapter chapter = chapterRepository.findById(request.getChapterId())
	            .orElseThrow(() -> new EntityNotFoundException("Chapter not found with ID: " + request.getChapterId()));
	    
	    ReadingProgress progress = progressRepository.findByUserIdAndStoryId(user.getId(), story.getId())
	            .orElse(new ReadingProgress());

	    progress.setUser(user);
	    progress.setStory(story);
	    progress.setChapter(chapter);
	    progress.setLastReadPage(request.getLastPage());
	    progress.setLastReadAt(Instant.now());

	    progressRepository.save(progress);
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public ReadingProgressResponse getProgressByStory(Long storyId) {
		User currentUser = getCurrentUser();
		
		Story story = storyRepository.findById(storyId)
				.orElseThrow(() -> new EntityNotFoundException("Story not found!"));
		
		ReadingProgress progress = progressRepository.findByUserIdAndStoryId(currentUser.getId(), story.getId())
				.orElseThrow(() -> new EntityNotFoundException("No reading progress found for this story!"));

        return mapToResponse(progress);
	}
	
	private ReadingProgressResponse mapToResponse(ReadingProgress progress) {
        ReadingProgressResponse response = new ReadingProgressResponse();
        response.setStoryId(progress.getStory().getId());
        response.setStoryTitle(progress.getStory().getTitle());
        response.setCoverUrl(progress.getStory().getCoverUrl());
        response.setChapterId(progress.getChapter().getId());
        response.setChapterNumber(progress.getChapter().getChapterNumber());
        response.setChapterTitle(progress.getChapter().getTitle());
        response.setLastPage(progress.getLastReadPage());
        response.setLastReadAt(progress.getLastReadAt());
        return response;
    }
}