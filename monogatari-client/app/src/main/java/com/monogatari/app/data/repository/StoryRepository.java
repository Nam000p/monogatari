package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.model.common.PageResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import java.util.List;
import retrofit2.Call;

public class StoryRepository {
    private final StoryApi storyApi;

    public StoryRepository(StoryApi storyApi) {
        this.storyApi = storyApi;
    }

    public Call<PageResponse<StoryResponse>> getStories(String search, int page, int size) {
        return storyApi.getStories(search, page, size);
    }

    public Call<StoryResponse> getStoryDetails(Long id) {
        return storyApi.getStoryDetails(id);
    }

    public Call<List<StoryResponse>> getStoriesByGenre(Long genreId) {
        return storyApi.getStoriesByGenre(genreId);
    }

    public Call<List<ChapterResponse>> getChapters(Long storyId) {
        return storyApi.getStoryChapters(storyId);
    }

    public Call<Void> incrementViewCount(Long storyId) {
        return storyApi.incrementViewCount(storyId);
    }

    public Call<Void> syncAverageRating(Long storyId) {
        return storyApi.syncAverageRating(storyId);
    }

    public Call<PageResponse<StoryResponse>> getLatestStories(int page, int size) {
        return storyApi.getLatestStories(page, size);
    }

    public Call<PageResponse<StoryResponse>> getTopViewedStories(int page, int size) {
        return storyApi.getTopViewedStories(page, size);
    }

    public Call<PageResponse<StoryResponse>> getTopRatedStories(int page, int size) {
        return storyApi.getTopRatedStories(page, size);
    }
}