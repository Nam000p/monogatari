package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.api.UserApi;
import com.monogatari.app.data.model.chapter.ChapterResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.model.user.UserProfileResponse;
import com.monogatari.app.data.repository.FollowRepository;
import com.monogatari.app.data.repository.StoryRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryDetailViewModel extends ViewModel {

    private final StoryRepository storyRepository;
    private final FollowRepository followRepository;

    private final MutableLiveData<StoryResponse> storyDetails = new MutableLiveData<>();
    private final MutableLiveData<List<ChapterResponse>> chapters = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFollowed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUserPremium = new MutableLiveData<>(false);

    public StoryDetailViewModel(StoryRepository storyRepository, FollowRepository followRepository) {
        this.storyRepository = storyRepository;
        this.followRepository = followRepository;
    }

    public LiveData<StoryResponse> getStoryDetails() { return storyDetails; }
    public LiveData<List<ChapterResponse>> getChapters() { return chapters; }
    public LiveData<Boolean> getIsFollowed() { return isFollowed; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchAllStoryData(Long storyId) {
        isLoading.setValue(true);

        storyRepository.getStoryDetails(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<StoryResponse> call, @NonNull Response<StoryResponse> response) {
                if (response.isSuccessful()) storyDetails.setValue(response.body());
                else errorMessage.setValue("Error: " + response.code());
                isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<StoryResponse> call, @NonNull Throwable t) {
                errorMessage.setValue(t.getMessage());
                isLoading.setValue(false);
            }
        });

        storyRepository.getChapters(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ChapterResponse>> call, @NonNull Response<List<ChapterResponse>> response) {
                if (response.isSuccessful()) chapters.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ChapterResponse>> call, @NonNull Throwable t) {}
        });

        // 3. Check Follow Status
        followRepository.checkFollowStatus(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) isFollowed.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {}
        });

        storyRepository.incrementViewCount(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {}
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
        });
    }

    public void toggleFollow(Long storyId) {
        followRepository.toggleFollow(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Boolean current = isFollowed.getValue();
                    isFollowed.setValue(current == null || !current);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                errorMessage.setValue("Action failed");
            }
        });
    }

    public LiveData<Boolean> getIsUserPremium() { return isUserPremium; }

    public void checkUserPremiumStatus(UserApi userApi) {
        userApi.getMyProfile().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isUserPremium.setValue(response.body().isPremium());
                }
            }
            @Override public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {}
        });
    }
}