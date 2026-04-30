package com.monogatari.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.monogatari.app.data.model.common.PageResponse;
import com.monogatari.app.data.model.story.StoryResponse;
import com.monogatari.app.data.repository.StoryRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final StoryRepository repository;

    private final MutableLiveData<List<StoryResponse>> trendingStories = new MutableLiveData<>();
    private final MutableLiveData<List<StoryResponse>> newestStories = new MutableLiveData<>();
    private final MutableLiveData<List<StoryResponse>> topRatedStories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HomeViewModel(StoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<StoryResponse>> getTrendingStories() { return trendingStories; }
    public LiveData<List<StoryResponse>> getNewestStories() { return newestStories; }
    public LiveData<List<StoryResponse>> getTopRatedStories() { return topRatedStories; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void fetchAllHomeData() {
        isLoading.setValue(true);
        fetchTrending();
        fetchNewest();
        fetchTopRated();
    }

    private void fetchTrending() {
        repository.getTopViewedStories(0, 10).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Response<PageResponse<StoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    trendingStories.setValue(response.body().getContent());
                }
                checkLoadingStatus();
            }
            @Override public void onFailure(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Throwable t) { checkLoadingStatus(); }
        });
    }

    private void fetchNewest() {
        repository.getLatestStories(0, 10).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Response<PageResponse<StoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newestStories.setValue(response.body().getContent());
                }
                checkLoadingStatus();
            }
            @Override public void onFailure(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Throwable t) { checkLoadingStatus(); }
        });
    }

    private void fetchTopRated() {
        repository.getTopRatedStories(0, 10).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Response<PageResponse<StoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    topRatedStories.setValue(response.body().getContent());
                }
                checkLoadingStatus();
            }
            @Override public void onFailure(@NonNull Call<PageResponse<StoryResponse>> call, @NonNull Throwable t) { checkLoadingStatus(); }
        });
    }

    private void checkLoadingStatus() {
        isLoading.setValue(false);
    }
}