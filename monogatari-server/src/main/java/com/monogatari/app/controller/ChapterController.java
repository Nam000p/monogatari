package com.monogatari.app.controller;

import com.monogatari.app.dto.chapter.ChapterRequest;
import com.monogatari.app.dto.chapter.ChapterResponse;
import com.monogatari.app.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories/{storyId}/chapters")
@RequiredArgsConstructor
public class ChapterController {
	private final ChapterService chapterService;

	@GetMapping("/{chapterId}")
    public ResponseEntity<ChapterResponse> getChapter(@PathVariable Long storyId, @PathVariable Long chapterId) {
		ChapterResponse response = chapterService.getChapterDetails(chapterId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
	@PostMapping
	public ResponseEntity<ChapterResponse> createChapter(
	        @PathVariable Long storyId, 
	        @Valid @RequestBody ChapterRequest request) {
		ChapterResponse response = chapterService.createChapter(storyId, request);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}