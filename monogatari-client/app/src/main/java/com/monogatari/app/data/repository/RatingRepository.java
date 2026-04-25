package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.RatingApi;
import com.monogatari.app.data.model.rating.RatingRequest;
import com.monogatari.app.data.model.rating.RatingResponse;

import java.util.List;

import retrofit2.Call;

public class RatingRepository {
    private final RatingApi ratingApi;

    public RatingRepository(RatingApi ratingApi) {
        this.ratingApi = ratingApi;
    }

    public Call<String> rateStory(Long storyId, RatingRequest request) {
        return ratingApi.rateStory(storyId, request);
    }

    public Call<List<RatingResponse>> getStoryRatings(Long storyId) {
        return ratingApi.getStoryRatings(storyId);
    }

    public Call<RatingResponse> getMyRating(Long storyId) {
        return ratingApi.getMyRating(storyId);
    }
}