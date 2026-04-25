package com.monogatari.app.service;

import com.monogatari.app.dto.progress.ReadingProgressRequest;
import com.monogatari.app.dto.progress.ReadingProgressResponse;

public interface ReadingProgressService {
    void updateProgress(Long storyId, ReadingProgressRequest request);
    
    ReadingProgressResponse getProgressByStory(Long storyId);
}