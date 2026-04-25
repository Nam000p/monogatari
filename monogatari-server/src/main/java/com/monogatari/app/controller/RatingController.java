package com.monogatari.app.controller;

import com.monogatari.app.dto.rating.RatingRequest;
import com.monogatari.app.dto.rating.RatingResponse;
import com.monogatari.app.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories/{storyId}/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<String> rateStory(@PathVariable Long storyId, @Valid @RequestBody RatingRequest request) {
        ratingService.rateStory(storyId, request);
        return new ResponseEntity<>("Rating submitted successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<RatingResponse>> getStoryRatings(@PathVariable Long storyId) {
    	List<RatingResponse> responses = ratingService.getStoryRatings(storyId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<RatingResponse> getMyRating(@PathVariable Long storyId) {
    	RatingResponse response = ratingService.getUserRatingForStory(storyId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}