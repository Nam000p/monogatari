package com.monogatari.app.controller;

import com.monogatari.app.dto.chapter.ChapterResponse;
import com.monogatari.app.dto.story.StoryResponse;
import com.monogatari.app.service.ChapterService;
import com.monogatari.app.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {
	private final StoryService storyService;
	
	private final ChapterService chapterService;

	@GetMapping
    public ResponseEntity<Page<StoryResponse>> getStories(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StoryResponse> responses;
        if (search != null && !search.trim().isEmpty()) {
            responses = storyService.searchStories(search, pageable);
        } else {
            responses = storyService.getAllStories(pageable);
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
	@GetMapping("/{storyId}")
    public ResponseEntity<StoryResponse> getStoryDetails(@PathVariable Long storyId) {
        StoryResponse response = storyService.getStoryById(storyId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
	@GetMapping("/genre/{genreId}")
    public ResponseEntity<Page<StoryResponse>> getStoriesByGenre(
            @PathVariable Long genreId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<StoryResponse> responses = storyService.getStoriesByGenre(genreId, pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
	@GetMapping("/{storyId}/chapters")
    public ResponseEntity<List<ChapterResponse>> getStoryChapters(@PathVariable Long storyId) {
        List<ChapterResponse> responses = chapterService.getChaptersByStoryId(storyId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

	@PostMapping("/{storyId}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long storyId) {
        storyService.updateViewCount(storyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	@PutMapping("/{storyId}/rating/sync")
    public ResponseEntity<Void> syncAverageRating(@PathVariable Long storyId) {
        storyService.updateAverageRating(storyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@GetMapping("/latest")
    public ResponseEntity<Page<StoryResponse>> getLatestStories(
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(storyService.getLatestStories(pageable));
    }

    @GetMapping("/top-views")
    public ResponseEntity<Page<StoryResponse>> getTopViewedStories(
            @PageableDefault(size = 20, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
    	Page<StoryResponse> responses = storyService.getTopViewedStories(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<Page<StoryResponse>> getTopRatedStories(
            @PageableDefault(size = 20, sort = "averageRating", direction = Sort.Direction.DESC) Pageable pageable) {
    	Page<StoryResponse> responses = storyService.getTopRatedStories(pageable);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}