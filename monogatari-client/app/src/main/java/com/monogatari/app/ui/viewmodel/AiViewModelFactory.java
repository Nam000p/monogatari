package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.repository.AiRepository;

public class AiViewModelFactory implements ViewModelProvider.Factory {
    private final AiRepository repository;
    public AiViewModelFactory(AiRepository repository) { this.repository = repository; }
    @NonNull
    @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AiViewModel(repository);
    }
}