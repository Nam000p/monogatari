package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.repository.RatingRepository;

public class RatingViewModelFactory implements ViewModelProvider.Factory {
    private final RatingRepository repository;

    public RatingViewModelFactory(RatingRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RatingViewModel.class)) {
            return (T) new RatingViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}