package com.monogatari.app.controller;

import com.monogatari.app.dto.progress.ReadingProgressRequest;
import com.monogatari.app.dto.progress.ReadingProgressResponse;
import com.monogatari.app.service.ReadingProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories/{storyId}/progress")
@RequiredArgsConstructor
public class ReadingProgressController {
	private final ReadingProgressService progressService;
	
	@PostMapping
	public ResponseEntity<String> updateProgress(@PathVariable Long storyId, 
			@Valid @RequestBody ReadingProgressRequest request) {
		progressService.updateProgress(storyId, request);
		return new ResponseEntity<>("Updated progress successfully!", HttpStatus.OK);
	}
	
	@GetMapping
	public ResponseEntity<ReadingProgressResponse> getProgress(@PathVariable Long storyId) {
		ReadingProgressResponse response = progressService.getProgressByStory(storyId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}