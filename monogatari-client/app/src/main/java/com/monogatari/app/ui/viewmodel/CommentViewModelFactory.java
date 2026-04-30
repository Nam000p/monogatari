package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.monogatari.app.data.repository.CommentRepository;

public class CommentViewModelFactory implements ViewModelProvider.Factory {
    private final CommentRepository repository;

    public CommentViewModelFactory(CommentRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CommentViewModel.class)) {
            return (T) new CommentViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}