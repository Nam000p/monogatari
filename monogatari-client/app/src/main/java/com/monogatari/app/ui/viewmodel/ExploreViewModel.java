package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.model.common.PageResponse;
import com.monogatari.app.data.repository.StoryRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreViewModel extends ViewModel {
    private final StoryRepository repository;
    private final MutableLiveData<List<StoryResponse>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ExploreViewModel(StoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<StoryResponse>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void searchStories(String query) {
        isLoading.setValue(true);
        repository.getStories(query, 0, 50).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Response<PageResponse<StoryResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    searchResults.setValue(response.body().getContent());
                } else {
                    errorMessage.setValue("Search failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}