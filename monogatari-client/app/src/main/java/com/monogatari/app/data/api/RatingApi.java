package com.monogatari.app.data.api;

import com.monogatari.app.data.model.rating.RatingRequest;
import com.monogatari.app.data.model.rating.RatingResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RatingApi {
    @POST("stories/{storyId}/ratings")
    Call<String> rateStory(
            @Path("storyId") Long storyId,
            @Body RatingRequest request
    );

    @GET("stories/{storyId}/ratings")
    Call<List<RatingResponse>> getStoryRatings(@Path("storyId") Long storyId);

    @GET("stories/{storyId}/ratings/me")
    Call<RatingResponse> getMyRating(@Path("storyId") Long storyId);
}