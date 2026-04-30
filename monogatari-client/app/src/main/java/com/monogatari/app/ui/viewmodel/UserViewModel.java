package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.follow.FollowResponse;
import com.monogatari.app.data.model.progress.ReadingProgressResponse;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.repository.FollowRepository;
import com.monogatari.app.data.repository.ReadingProgressRepository;
import com.monogatari.app.data.repository.UserRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReadingProgressRepository progressRepository;

    private final MutableLiveData<UserProfileResponse> profile = new MutableLiveData<>();
    private final MutableLiveData<List<FollowResponse>> followedList = new MutableLiveData<>();
    private final MutableLiveData<List<ReadingProgressResponse>> readingList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository, FollowRepository followRepository, ReadingProgressRepository progressRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.progressRepository = progressRepository;
    }

    public LiveData<UserProfileResponse> getProfile() { return profile; }
    public LiveData<List<FollowResponse>> getFollowedList() { return followedList; }
    public LiveData<List<ReadingProgressResponse>> getReadingList() { return readingList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public void fetchAllData() {
        isLoading.setValue(true);
        fetchProfile();
        fetchFollowedStories();
        fetchReadingProgress();
    }

    public void fetchProfile() {
        userRepository.getMyProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    profile.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }

    private void fetchFollowedStories() {
        followRepository.getMyFollowedStories().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<FollowResponse>> call, @NonNull Response<List<FollowResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    followedList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FollowResponse>> call, @NonNull Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    private void fetchReadingProgress() {
        progressRepository.getAllProgress().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ReadingProgressResponse>> call, @NonNull Response<List<ReadingProgressResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    readingList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReadingProgressResponse>> call, @NonNull Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }
}