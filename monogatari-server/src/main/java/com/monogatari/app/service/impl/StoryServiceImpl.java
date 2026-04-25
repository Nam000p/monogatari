package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.RateLimited;
import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.story.StoryResponse;
import com.monogatari.app.entity.Genre;
import com.monogatari.app.entity.Story;
import com.monogatari.app.entity.User;
import com.monogatari.app.repository.RatingRepository;
import com.monogatari.app.repository.StoryRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.StoryService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl extends BaseService implements StoryService {
    private final StoryRepository storyRepository;
    private final RatingRepository ratingRepository;
    private final UserService userService;

    @Override
    protected UserService getUserService() {
        return userService;
    }

    private int getCurrentUserAge() {
        User user = getCurrentUser();
        return (user != null) ? user.calculateAge() : 0;
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public Page<StoryResponse> getAllStories(Pageable pageable) {
        return storyRepository.findAllWithGenresAndAgeLimit(getCurrentUserAge(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public Page<StoryResponse> searchStories(String query, Pageable pageable) {
        return storyRepository.findByTitleContainingIgnoreCaseAndAgeLimitLessThanEqual(query, getCurrentUserAge(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public StoryResponse getStoryById(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found!"));

        if (story.getAgeLimit() > getCurrentUserAge()) {
            throw new AccessDeniedException("This story is not suitable for your age.");
        }

        return mapToResponse(story);
    }

    @Override
    @Transactional(readOnly = true)
    @TrackExecutionTime
    public Page<StoryResponse> getStoriesByGenre(Long genreId, Pageable pageable) {
        return storyRepository.findByGenreIdAndAgeLimit(genreId, getCurrentUserAge(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @RateLimited(maxRequests = 10, timeWindowMs = 60000)
    public void updateViewCount(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found!"));
        story.setViewCount(story.getViewCount() + 1);
        storyRepository.save(story);
    }

    @Override
    @Transactional
    @TrackExecutionTime
    public void updateAverageRating(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new EntityNotFoundException("Story not found!"));
        Double average = ratingRepository.calculateAverageRatingByStoryId(storyId);
        story.setAverageRating(average != null ? average : 0.0);
        storyRepository.save(story);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryResponse> getLatestStories(Pageable pageable) {
        return storyRepository.findAllByAgeLimitLessThanEqualOrderByUpdatedAtDesc(getCurrentUserAge(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryResponse> getTopViewedStories(Pageable pageable) {
        return storyRepository.findAllByAgeLimitLessThanEqualOrderByViewCountDesc(getCurrentUserAge(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryResponse> getTopRatedStories(Pageable pageable) {
        return storyRepository.findAllByAgeLimitLessThanEqualOrderByAverageRatingDesc(getCurrentUserAge(), pageable)
                .map(this::mapToResponse);
    }

    private StoryResponse mapToResponse(Story story) {
        StoryResponse response = new StoryResponse();
        response.setId(story.getId());
        response.setTitle(story.getTitle());
        response.setDescription(story.getDescription());
        response.setCoverUrl(story.getCoverUrl());
        response.setAuthorName(story.getAuthor() != null ? story.getAuthor().getName() : "Unknown");
        response.setType(story.getType());
        response.setStatus(story.getStatus());
        response.setAgeLimit(story.getAgeLimit());
        response.setAverageRating(story.getAverageRating());
        
        if (story.getGenre() != null && !story.getGenre().isEmpty()) {
            response.setGenres(story.getGenre().stream()
                .map(Genre::getName)
                .collect(Collectors.toList()));
        } else {
            response.setGenres(new ArrayList<>());
        }
        return response;
    }
}