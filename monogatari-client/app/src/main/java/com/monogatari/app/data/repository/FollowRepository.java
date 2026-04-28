package com.monogatari.app.data.repository;

import com.monogatari.app.data.api.FollowApi;
import com.monogatari.app.data.model.follow.FollowResponse;

import java.util.List;

import retrofit2.Call;

public class FollowRepository {
    private final FollowApi followApi;

    public FollowRepository(FollowApi followApi) {
        this.followApi = followApi;
    }

    public Call<String> toggleFollow(Long storyId) {
        return followApi.toggleFollow(storyId);
    }

    public Call<Boolean> checkFollowStatus(Long storyId) {
        return followApi.checkFollowStatus(storyId);
    }

    public Call<List<FollowResponse>> getMyFollowedStories() {
        return followApi.getMyFollowedStories();
    }
}