package com.monogatari.app.data.api;

import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.model.common.PageResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StoryApi {
    @GET("stories")
    Call<PageResponse<StoryResponse>> getStories(
            @Query("search") String search,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("stories/latest")
    Call<PageResponse<StoryResponse>> getLatestStories(
            @Query("page") int page,
            @Query("size") int size
    );
    @GET("stories/{storyId}")
    Call<StoryResponse> getStoryDetails(@Path("storyId") Long storyId);

    @GET("stories/genre/{genreId}")
    Call<List<StoryResponse>> getStoriesByGenre(@Path("genreId") Long genreId);

    @GET("stories/{storyId}/chapters")
    Call<List<ChapterResponse>> getStoryChapters(@Path("storyId") Long storyId);

    @POST("stories/{storyId}/view")
    Call<Void> incrementViewCount(@Path("storyId") Long storyId);

    @PUT("stories/{storyId}/rating/sync")
    Call<Void> syncAverageRating(@Path("storyId") Long storyId);
}