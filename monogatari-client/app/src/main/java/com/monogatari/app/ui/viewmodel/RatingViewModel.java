package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.rating.RatingRequest;
import com.monogatari.app.data.model.rating.RatingResponse;
import com.monogatari.app.data.repository.RatingRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingViewModel extends ViewModel {
    private final RatingRepository repository;
    private final MutableLiveData<List<RatingResponse>> ratings = new MutableLiveData<>();
    private final MutableLiveData<RatingResponse> myRating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public RatingViewModel(RatingRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<RatingResponse>> getRatings() { return ratings; }
    public LiveData<RatingResponse> getMyRating() { return myRating; }
    public LiveData<Boolean> getIsSuccess() { return isSuccess; }
    public LiveData<String> getError() { return error; }

    public void fetchRatings(Long storyId) {
        repository.getStoryRatings(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<RatingResponse>> call, @NonNull Response<List<RatingResponse>> response) {
                if (response.isSuccessful()) ratings.setValue(response.body());
            }
            @Override public void onFailure(@NonNull Call<List<RatingResponse>> call, @NonNull Throwable t) { error.setValue(t.getMessage()); }
        });
    }

    public void fetchMyRating(Long storyId) {
        repository.getMyRating(storyId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RatingResponse> call, @NonNull Response<RatingResponse> response) {
                if (response.isSuccessful()) myRating.setValue(response.body());
            }
            @Override public void onFailure(@NonNull Call<RatingResponse> call, @NonNull Throwable t) {}
        });
    }

    public void submitRating(Long storyId, int score, String review) {
        RatingRequest request = new RatingRequest();
        request.setScore(score);
        request.setReview(review);

        repository.rateStory(storyId, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    isSuccess.setValue(true);
                    fetchRatings(storyId);
                } else {
                    error.setValue("Failed to submit rating");
                }
            }
            @Override public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) { error.setValue(t.getMessage()); }
        });
    }
}