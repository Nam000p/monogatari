package com.monogatari.app.controller;

import com.monogatari.app.dto.genre.GenreResponse;
import com.monogatari.app.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {
	private final GenreService genreService;
	
	@GetMapping
	public ResponseEntity<List<GenreResponse>> getAllGenre() {
		List<GenreResponse> responses = genreService.getAllGenres();
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}
}