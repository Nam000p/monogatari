package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.author.AuthorResponse;
import com.monogatari.app.dto.story.StoryResponse;
import com.monogatari.app.entity.Author;
import com.monogatari.app.entity.Genre;
import com.monogatari.app.entity.Story;
import com.monogatari.app.repository.AuthorRepository;
import com.monogatari.app.service.AuthorService;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl extends BaseService implements AuthorService {
	private final AuthorRepository authorRepository;
	
	private final UserService userService;
	
	@Override
	protected UserService getUserService() {
		return userService;
	}

	@Override
	@Transactional
	@TrackExecutionTime
	public List<StoryResponse> getStoriesByAuthor(Long authorId) {
		getCurrentUser();
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Author not found!"));
        return author.getStories().stream()
                .map(this::mapToStoryResponse)
                .collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public AuthorResponse getAuthorDetails(Long authorId) {
		getCurrentUser();
		Author author = authorRepository.findById(authorId)
				.orElseThrow(() -> new EntityNotFoundException("Author not found!"));
        return mapToAuthorResponse(author);
	}

	private AuthorResponse mapToAuthorResponse(Author author) {
        AuthorResponse response = new AuthorResponse();
        response.setId(author.getId());
        response.setName(author.getName());
        response.setBio(author.getBio());
        response.setAvatarUrl(author.getAvatarUrl());
        return response;
    }

    private StoryResponse mapToStoryResponse(Story story) {
        StoryResponse response = new StoryResponse();
        response.setId(story.getId());
        response.setTitle(story.getTitle());
        response.setDescription(story.getDescription());
        response.setCoverUrl(story.getCoverUrl());
        response.setAuthorName(story.getAuthor().getName());
        response.setType(story.getType());
        response.setStatus(story.getStatus());
		response.setAgeLimit(story.getAgeLimit());
        response.setAverageRating(story.getAverageRating());
        if (story.getGenre() != null) {
            response.setGenres(story.getGenre().stream()
                .map(Genre::getName)
                .collect(Collectors.toList()));
        }
        return response;
    }
}