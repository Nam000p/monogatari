package com.monogatari.app.controller;

import com.monogatari.app.dto.author.AuthorResponse;
import com.monogatari.app.dto.story.StoryResponse;
import com.monogatari.app.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
	private final AuthorService authorService;
	
	@GetMapping("/{authorId}")
	public ResponseEntity<AuthorResponse> getAuthorDetail(@PathVariable Long authorId) {
		AuthorResponse response = authorService.getAuthorDetails(authorId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/{authorId}/stories")
	public ResponseEntity<List<StoryResponse>> getStories(@PathVariable Long authorId) {
		List<StoryResponse> responses = authorService.getStoriesByAuthor(authorId);
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}
}