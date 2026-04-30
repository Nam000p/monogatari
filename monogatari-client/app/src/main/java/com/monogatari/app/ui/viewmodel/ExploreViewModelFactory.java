package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.repository.StoryRepository;

public class ExploreViewModelFactory implements ViewModelProvider.Factory {
    private final StoryRepository storyRepository;

    public ExploreViewModelFactory(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExploreViewModel.class)) {
            return (T) new ExploreViewModel(storyRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}