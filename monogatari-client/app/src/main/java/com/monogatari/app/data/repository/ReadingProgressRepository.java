package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.ReadingProgressApi;
import com.monogatari.app.data.model.progress.ReadingProgressRequest;
import com.monogatari.app.data.model.progress.ReadingProgressResponse;

import retrofit2.Call;

public class ReadingProgressRepository {
    private final ReadingProgressApi progressApi;

    public ReadingProgressRepository(ReadingProgressApi progressApi) {
        this.progressApi = progressApi;
    }

    public Call<String> updateProgress(Long storyId, ReadingProgressRequest request) {
        return progressApi.updateProgress(storyId, request);
    }

    public Call<ReadingProgressResponse> getProgress(Long storyId) {
        return progressApi.getProgress(storyId);
    }
}