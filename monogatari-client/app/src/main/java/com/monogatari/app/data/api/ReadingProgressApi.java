package com.monogatari.app.data.api;

import com.monogatari.app.data.model.progress.ReadingProgressRequest;
import com.monogatari.app.data.model.progress.ReadingProgressResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReadingProgressApi {
    @POST("stories/{storyId}/progress")
    Call<String> updateProgress(
            @Path("storyId") Long storyId,
            @Body ReadingProgressRequest request
    );

    @GET("stories/{storyId}/progress")
    Call<ReadingProgressResponse> getProgress(@Path("storyId") Long storyId);
}