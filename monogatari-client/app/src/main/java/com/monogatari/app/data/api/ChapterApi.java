package com.monogatari.app.data.api;

import com.monogatari.app.data.model.chapter.ChapterResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChapterApi {
    @GET("stories/{storyId}/chapters/{chapterId}")
    Call<ChapterResponse> getChapter(
            @Path("storyId") Long storyId,
            @Path("chapterId") Long chapterId
    );
}