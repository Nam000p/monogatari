package com.monogatari.app.service;

import com.monogatari.app.dto.genre.GenreResponse;

import java.util.List;

public interface GenreService {
	List<GenreResponse> getAllGenres();
	
	void createGenre(String name);
	
	void deleteGenre(Long genreId);
}