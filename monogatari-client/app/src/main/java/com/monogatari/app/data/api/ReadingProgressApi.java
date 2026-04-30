package com.monogatari.app.data.api;

import com.monogatari.app.data.model.progress.ReadingProgressRequest;
import com.monogatari.app.data.model.progress.ReadingProgressResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReadingProgressApi {
    @POST("progress/stories/{storyId}")
    Call<String> updateProgress(
            @Path("storyId") Long storyId,
            @Body ReadingProgressRequest request
    );

    @GET("progress/stories/{storyId}")
    Call<ReadingProgressResponse> getProgress(@Path("storyId") Long storyId);

    @GET("progress/me")
    Call<List<ReadingProgressResponse>> getAllProgress();
}