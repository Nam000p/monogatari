package com.monogatari.app.service;

import com.monogatari.app.dto.progress.ReadingProgressRequest;
import com.monogatari.app.dto.progress.ReadingProgressResponse;

import java.util.List;

public interface ReadingProgressService {
    void updateProgress(Long storyId, ReadingProgressRequest request);
    
    ReadingProgressResponse getProgressByStory(Long storyId);

    List<ReadingProgressResponse> getAllMyProgress();
}