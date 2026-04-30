package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.monogatari.app.data.repository.FollowRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import com.monogatari.app.data.repository.UserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReadingProgressRepository progressRepository;

    public UserViewModelFactory(UserRepository userRepository, FollowRepository followRepository, ReadingProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.progressRepository = progressRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository, followRepository, progressRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}