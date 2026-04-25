package com.monogatari.app.data.api;

import com.monogatari.app.data.model.follow.FollowResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FollowApi {
    @POST("stories/{storyId}/follow")
    Call<String> toggleFollow(@Path("storyId") Long storyId);

    @GET("stories/{storyId}/follow/check")
    Call<Boolean> checkFollowStatus(@Path("storyId") Long storyId);

    @GET("me/follows")
    Call<List<FollowResponse>> getMyFollowedStories();
}
