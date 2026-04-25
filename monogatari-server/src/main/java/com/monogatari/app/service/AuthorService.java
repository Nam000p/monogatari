package com.monogatari.app.service;

import com.monogatari.app.dto.author.AuthorResponse;
import com.monogatari.app.dto.story.StoryResponse;

import java.util.List;

public interface AuthorService {
	List<StoryResponse> getStoriesByAuthor(Long authorId);
	
	AuthorResponse getAuthorDetails(Long authorId);
}