package com.monogatari.app.service.impl;

import com.monogatari.app.annotation.TrackExecutionTime;
import com.monogatari.app.dto.genre.GenreResponse;
import com.monogatari.app.entity.Genre;
import com.monogatari.app.repository.GenreRepository;
import com.monogatari.app.service.BaseService;
import com.monogatari.app.service.GenreService;
import com.monogatari.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl extends BaseService implements GenreService {
	private final GenreRepository genreRepository;
	
	private final UserService userService;
	
	@Override
	protected UserService getUserService() {
		return userService;
	}
	
	@Override
	@Transactional(readOnly = true)
	@TrackExecutionTime
	public List<GenreResponse> getAllGenres() {
		getCurrentUser();
        return genreRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void createGenre(String name) {
		if (genreRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Genre already exists!");
        }
        Genre genre = Genre.builder().name(name).build();
        genreRepository.save(genre);
	}

	@Override
	@Transactional
	public void deleteGenre(Long genreId) {
		if (!genreRepository.existsById(genreId)) {
            throw new EntityNotFoundException("Genre not found!");
        }
        genreRepository.deleteById(genreId);

	}

	private GenreResponse mapToResponse(Genre genre) {
        GenreResponse response = new GenreResponse();
        response.setId(genre.getId());
        response.setName(genre.getName());
        return response;
    }
}