package com.monogatari.app.ui.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.api.ApiClient;
import com.monogatari.app.data.api.FollowApi;
import com.monogatari.app.data.api.StoryApi;
import com.monogatari.app.data.repository.FollowRepository;
import com.monogatari.app.data.repository.StoryRepository;

public class StoryDetailViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public StoryDetailViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StoryDetailViewModel.class)) {
            StoryApi storyApi = ApiClient.getClient(context).create(StoryApi.class);
            FollowApi followApi = ApiClient.getClient(context).create(FollowApi.class);

            StoryRepository storyRepo = new StoryRepository(storyApi);
            FollowRepository followRepo = new FollowRepository(followApi);

            return (T) new StoryDetailViewModel(storyRepo, followRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}